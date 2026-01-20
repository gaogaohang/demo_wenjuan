package com.couple.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.couple.platform.dto.order.*;
import com.couple.platform.entity.*;
import com.couple.platform.enums.ErrorCode;
import com.couple.platform.exception.BusinessException;
import com.couple.platform.mapper.*;
import com.couple.platform.utils.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderEvaluationMapper orderEvaluationMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(OrderCreateRequest request) {
        Long creatorId = SecurityUtil.getCurrentUserId();
        User creator = userMapper.selectById(creatorId);
        if (creator == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (!creator.hasPaired()) {
            throw new BusinessException(ErrorCode.PARTNER_NOT_FOUND, "您还没有配对，无法创建订单");
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setCreatorId(creatorId);
        order.setReceiverId(creator.getPartnerId());
        order.setTitle(request.getTitle());
        order.setDescription(request.getDescription());
        order.setType(request.getType());
        order.setNote(request.getNote());
        order.setLocation(request.getLocation());
        order.setEstimatedTime(request.getEstimatedTime());
        order.setStatus("pending");

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            try {
                order.setImages(objectMapper.writeValueAsString(request.getImages()));
            } catch (JsonProcessingException e) {
                log.error("序列化图片列表失败", e);
            }
        }

        orderMapper.insert(order);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderCreateRequest.OrderItemRequest itemRequest : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setName(itemRequest.getName());
            orderItem.setDescription(itemRequest.getDescription());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(itemRequest.getUnitPrice());
            orderItem.setImageUrl(itemRequest.getImageUrl());
            orderItem.setNote(itemRequest.getNote());

            BigDecimal itemTotal = itemRequest.getUnitPrice()
                    .multiply(new BigDecimal(itemRequest.getQuantity()));
            orderItem.setTotalPrice(itemTotal);
            totalAmount = totalAmount.add(itemTotal);

            orderItemMapper.insert(orderItem);
        }

        order.setTotalAmount(totalAmount);
        orderMapper.updateById(order);

        log.info("订单创建成功: orderId={}, orderNo={}, creatorId={}", order.getId(), order.getOrderNo(), creatorId);

        return buildOrderResponse(order);
    }

    public List<OrderResponse> getUserOrders(String status) {
        Long userId = SecurityUtil.getCurrentUserId();

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getCreatorId, userId)
                .or()
                .eq(Order::getReceiverId, userId);

        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status);
        }

        wrapper.orderByDesc(Order::getCreatedTime);

        List<Order> orders = orderMapper.selectList(wrapper);

        return orders.stream()
                .map(this::buildOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderDetail(Long orderId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        if (!order.getCreatorId().equals(userId) && !order.getReceiverId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看此订单");
        }

        return buildOrderResponse(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        if (!order.getCreatorId().equals(userId) && !order.getReceiverId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此订单");
        }

        validateStatusTransition(order, request.getStatus(), userId);

        String oldStatus = order.getStatus();
        order.setStatus(request.getStatus());

        LocalDateTime now = LocalDateTime.now();
        switch (request.getStatus()) {
            case "accepted":
                order.setAcceptedTime(now);
                break;
            case "completed":
                order.setCompletedTime(now);
                break;
        }

        orderMapper.updateById(order);

        log.info("订单状态更新成功: orderId={}, {} -> {}, userId={}",
                orderId, oldStatus, request.getStatus(), userId);

        return buildOrderResponse(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderResponse cancelOrder(Long orderId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        if (!order.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只有订单创建者可以取消订单");
        }

        if (!order.canCancel()) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        order.setStatus("cancelled");
        orderMapper.updateById(order);

        log.info("订单取消成功: orderId={}, userId={}", orderId, userId);

        return buildOrderResponse(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderResponse evaluateOrder(Long orderId, OrderEvaluationRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        if (!order.getCreatorId().equals(userId) && !order.getReceiverId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权评价此订单");
        }

        if (!order.canEvaluate()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "只有已完成的订单才能评价");
        }

        OrderEvaluation existing = orderEvaluationMapper.selectOne(
                new LambdaQueryWrapper<OrderEvaluation>()
                        .eq(OrderEvaluation::getOrderId, orderId)
                        .eq(OrderEvaluation::getEvaluatorId, userId)
        );
        if (existing != null) {
            throw new BusinessException(ErrorCode.EVALUATION_ALREADY_EXISTS);
        }

        OrderEvaluation evaluation = new OrderEvaluation();
        evaluation.setOrderId(orderId);
        evaluation.setEvaluatorId(userId);
        evaluation.setRating(request.getRating());
        evaluation.setComment(request.getComment());
        evaluation.setIsAnonymous(request.getIsAnonymous());

        try {
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                evaluation.setImages(objectMapper.writeValueAsString(request.getImages()));
            }
            if (request.getEmojis() != null && !request.getEmojis().isEmpty()) {
                evaluation.setEmojis(objectMapper.writeValueAsString(request.getEmojis()));
            }
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                evaluation.setTags(objectMapper.writeValueAsString(request.getTags()));
            }
        } catch (JsonProcessingException e) {
            log.error("序列化评价数据失败", e);
        }

        orderEvaluationMapper.insert(evaluation);

        log.info("订单评价成功: orderId={}, evaluatorId={}, rating={}", orderId, userId, request.getRating());

        return buildOrderResponse(order);
    }

    public List<OrderResponse.OrderEvaluationResponse> getOrderEvaluations(Long orderId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        if (!order.getCreatorId().equals(userId) && !order.getReceiverId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看此订单评价");
        }

        List<OrderEvaluation> evaluations = orderEvaluationMapper.selectList(
                new LambdaQueryWrapper<OrderEvaluation>()
                        .eq(OrderEvaluation::getOrderId, orderId)
                        .orderByDesc(OrderEvaluation::getCreatedTime)
        );

        return evaluations.stream()
                .map(this::buildEvaluationResponse)
                .collect(Collectors.toList());
    }

    private void validateStatusTransition(Order order, String newStatus, Long userId) {
        String currentStatus = order.getStatus();

        switch (newStatus) {
            case "accepted":
                if (!"pending".equals(currentStatus)) {
                    throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有待处理的订单才能接受");
                }
                if (!order.getReceiverId().equals(userId)) {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "只有接收者可以接受订单");
                }
                break;

            case "processing":
                if (!"accepted".equals(currentStatus)) {
                    throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有已接受的订单才能进入处理中状态");
                }
                if (!order.getReceiverId().equals(userId)) {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "只有接收者可以更新订单为处理中");
                }
                break;

            case "completed":
                if (!"processing".equals(currentStatus) && !"accepted".equals(currentStatus)) {
                    throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有处理中或已接受的订单才能完成");
                }
                if (!order.getReceiverId().equals(userId)) {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "只有接收者可以完成订单");
                }
                break;

            case "cancelled":
                if (!order.canCancel()) {
                    throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
                }
                break;

            default:
                throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID);
        }
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 10000));
        return "ORD" + timestamp + String.format("%04d", Integer.parseInt(random));
    }

    private OrderResponse buildOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setTitle(order.getTitle());
        response.setDescription(order.getDescription());
        response.setType(order.getType());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setNote(order.getNote());
        response.setLocation(order.getLocation());
        response.setEstimatedTime(order.getEstimatedTime());
        response.setAcceptedTime(order.getAcceptedTime());
        response.setCompletedTime(order.getCompletedTime());
        response.setCreatedTime(order.getCreatedTime());
        response.setUpdatedTime(order.getUpdatedTime());

        if (order.getImages() != null) {
            try {
                List<String> images = objectMapper.readValue(order.getImages(), new TypeReference<List<String>>() {});
                response.setImages(images);
            } catch (JsonProcessingException e) {
                log.error("反序列化图片列表失败", e);
                response.setImages(new ArrayList<>());
            }
        }

        User creator = userMapper.selectById(order.getCreatorId());
        if (creator != null) {
            OrderResponse.UserInfo creatorInfo = new OrderResponse.UserInfo();
            creatorInfo.setId(creator.getId());
            creatorInfo.setUsername(creator.getUsername());
            creatorInfo.setNickname(creator.getNickname());
            creatorInfo.setAvatarUrl(creator.getAvatarUrl());
            response.setCreator(creatorInfo);
        }

        if (order.getReceiverId() != null) {
            User receiver = userMapper.selectById(order.getReceiverId());
            if (receiver != null) {
                OrderResponse.UserInfo receiverInfo = new OrderResponse.UserInfo();
                receiverInfo.setId(receiver.getId());
                receiverInfo.setUsername(receiver.getUsername());
                receiverInfo.setNickname(receiver.getNickname());
                receiverInfo.setAvatarUrl(receiver.getAvatarUrl());
                response.setReceiver(receiverInfo);
            }
        }

        List<OrderItem> orderItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, order.getId())
                        .orderByAsc(OrderItem::getCreatedTime)
        );

        List<OrderResponse.OrderItemResponse> itemResponses = orderItems.stream().map(item -> {
            OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setName(item.getName());
            itemResponse.setDescription(item.getDescription());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setTotalPrice(item.getTotalPrice());
            itemResponse.setImageUrl(item.getImageUrl());
            itemResponse.setNote(item.getNote());
            return itemResponse;
        }).collect(Collectors.toList());
        response.setItems(itemResponses);

        List<OrderEvaluation> evaluations = orderEvaluationMapper.selectList(
                new LambdaQueryWrapper<OrderEvaluation>()
                        .eq(OrderEvaluation::getOrderId, order.getId())
                        .orderByDesc(OrderEvaluation::getCreatedTime)
        );

        List<OrderResponse.OrderEvaluationResponse> evaluationResponses = evaluations.stream()
                .map(this::buildEvaluationResponse)
                .collect(Collectors.toList());
        response.setEvaluations(evaluationResponses);

        return response;
    }

    private OrderResponse.OrderEvaluationResponse buildEvaluationResponse(OrderEvaluation evaluation) {
        OrderResponse.OrderEvaluationResponse response = new OrderResponse.OrderEvaluationResponse();
        response.setId(evaluation.getId());
        response.setRating(evaluation.getRating());
        response.setComment(evaluation.getComment());
        response.setIsAnonymous(evaluation.getIsAnonymous());
        response.setCreatedTime(evaluation.getCreatedTime());

        try {
            if (evaluation.getImages() != null) {
                response.setImages(objectMapper.readValue(evaluation.getImages(), new TypeReference<List<String>>() {}));
            }
            if (evaluation.getEmojis() != null) {
                response.setEmojis(objectMapper.readValue(evaluation.getEmojis(), new TypeReference<List<String>>() {}));
            }
            if (evaluation.getTags() != null) {
                response.setTags(objectMapper.readValue(evaluation.getTags(), new TypeReference<List<String>>() {}));
            }
        } catch (JsonProcessingException e) {
            log.error("反序列化评价数据失败", e);
        }

        if (!evaluation.getIsAnonymous()) {
            User evaluator = userMapper.selectById(evaluation.getEvaluatorId());
            if (evaluator != null) {
                OrderResponse.UserInfo evaluatorInfo = new OrderResponse.UserInfo();
                evaluatorInfo.setId(evaluator.getId());
                evaluatorInfo.setUsername(evaluator.getUsername());
                evaluatorInfo.setNickname(evaluator.getNickname());
                evaluatorInfo.setAvatarUrl(evaluator.getAvatarUrl());
                response.setEvaluator(evaluatorInfo);
            }
        }

        return response;
    }
}
