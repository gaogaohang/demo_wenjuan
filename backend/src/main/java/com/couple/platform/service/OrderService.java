package com.couple.platform.service;

import com.couple.platform.dto.order.*;
import com.couple.platform.entity.*;
import com.couple.platform.exception.BusinessException;
import com.couple.platform.repository.*;
import com.couple.platform.utils.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
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

/**
 * 订单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderEvaluationRepository orderEvaluationRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * 创建订单
     */
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        Long creatorId = SecurityUtil.getCurrentUserId();
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        
        // 检查用户是否已配对
        if (!creator.hasPaired()) {
            throw new BusinessException("您还没有配对，无法创建订单");
        }
        
        // 创建订单
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
        
        // 处理图片列表
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            try {
                order.setImages(objectMapper.writeValueAsString(request.getImages()));
            } catch (JsonProcessingException e) {
                log.error("序列化图片列表失败", e);
            }
        }
        
        orderRepository.save(order);
        
        // 创建订单项
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
            
            // 计算总价
            orderItem.calculateTotalPrice();
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
            
            orderItemRepository.save(orderItem);
        }
        
        // 更新订单总金额
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
        
        log.info("订单创建成功: orderId={}, orderNo={}, creatorId={}", order.getId(), order.getOrderNo(), creatorId);
        
        return buildOrderResponse(order);
    }
    
    /**
     * 获取用户订单列表
     */
    public List<OrderResponse> getUserOrders(String status) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findUserRelatedOrdersByStatus(userId, status);
        } else {
            orders = orderRepository.findUserRelatedOrders(userId);
        }
        
        return orders.stream()
                .map(this::buildOrderResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取订单详情
     */
    public OrderResponse getOrderDetail(Long orderId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        
        // 检查权限：只有创建者和接收者可以查看
        if (!order.getCreatorId().equals(userId) && !order.getReceiverId().equals(userId)) {
            throw new BusinessException("无权查看此订单");
        }
        
        return buildOrderResponse(order);
    }
    
    /**
     * 更新订单状态
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        
        // 检查权限
        if (!order.getCreatorId().equals(userId) && !order.getReceiverId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        // 验证状态转换
        validateStatusTransition(order, request.getStatus(), userId);
        
        // 更新状态
        String oldStatus = order.getStatus();
        order.setStatus(request.getStatus());
        
        // 设置特殊时间字段
        LocalDateTime now = LocalDateTime.now();
        switch (request.getStatus()) {
            case "accepted":
                order.setAcceptedTime(now);
                break;
            case "completed":
                order.setCompletedTime(now);
                break;
        }
        
        orderRepository.save(order);
        
        log.info("订单状态更新成功: orderId={}, {} -> {}, userId={}", 
                orderId, oldStatus, request.getStatus(), userId);
        
        return buildOrderResponse(order);
    }
    
    /**
     * 取消订单
     */
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        
        // 检查权限：只有创建者可以取消订单
        if (!order.getCreatorId().equals(userId)) {
            throw new BusinessException("只有订单创建者可以取消订单");
        }
        
        // 检查订单状态
        if (!order.canCancel()) {
            throw new BusinessException("当前状态的订单无法取消");
        }
        
        order.setStatus("cancelled");
        orderRepository.save(order);
        
        log.info("订单取消成功: orderId={}, userId={}", orderId, userId);
        
        return buildOrderResponse(order);
    }
    
    /**
     * 评价订单
     */
    @Transactional
    public OrderResponse evaluateOrder(Long orderId, OrderEvaluationRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        
        // 检查权限：创建者和接收者都可以评价
        if (!order.getCreatorId().equals(userId) && !order.getReceiverId().equals(userId)) {
            throw new BusinessException("无权评价此订单");
        }
        
        // 检查订单状态
        if (!order.canEvaluate()) {
            throw new BusinessException("只有已完成的订单才能评价");
        }
        
        // 检查是否已评价
        if (orderEvaluationRepository.existsByOrderIdAndEvaluatorId(orderId, userId)) {
            throw new BusinessException("您已经评价过此订单");
        }
        
        // 创建评价
        OrderEvaluation evaluation = new OrderEvaluation();
        evaluation.setOrderId(orderId);
        evaluation.setEvaluatorId(userId);
        evaluation.setRating(request.getRating());
        evaluation.setComment(request.getComment());
        evaluation.setIsAnonymous(request.getIsAnonymous());
        
        // 处理图片、表情和标签
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
        
        orderEvaluationRepository.save(evaluation);
        
        log.info("订单评价成功: orderId={}, evaluatorId={}, rating={}", orderId, userId, request.getRating());
        
        return buildOrderResponse(order);
    }
    
    /**
     * 验证状态转换
     */
    private void validateStatusTransition(Order order, String newStatus, Long userId) {
        String currentStatus = order.getStatus();
        
        // 基本状态流转规则
        switch (newStatus) {
            case "accepted":
                if (!"pending".equals(currentStatus)) {
                    throw new BusinessException("只有待处理的订单才能接受");
                }
                if (!order.getReceiverId().equals(userId)) {
                    throw new BusinessException("只有接收者可以接受订单");
                }
                break;
                
            case "processing":
                if (!"accepted".equals(currentStatus)) {
                    throw new BusinessException("只有已接受的订单才能进入处理中状态");
                }
                if (!order.getReceiverId().equals(userId)) {
                    throw new BusinessException("只有接收者可以更新订单为处理中");
                }
                break;
                
            case "completed":
                if (!"processing".equals(currentStatus) && !"accepted".equals(currentStatus)) {
                    throw new BusinessException("只有处理中或已接受的订单才能完成");
                }
                if (!order.getReceiverId().equals(userId)) {
                    throw new BusinessException("只有接收者可以完成订单");
                }
                break;
                
            case "cancelled":
                if (!order.canCancel()) {
                    throw new BusinessException("当前状态的订单无法取消");
                }
                break;
                
            default:
                throw new BusinessException("无效的订单状态");
        }
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 10000));
        return "ORD" + timestamp + String.format("%04d", Integer.parseInt(random));
    }
    
    /**
     * 构建订单响应
     */
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
        
        // 处理图片列表
        if (order.getImages() != null) {
            try {
                List<String> images = objectMapper.readValue(order.getImages(), List.class);
                response.setImages(images);
            } catch (JsonProcessingException e) {
                log.error("反序列化图片列表失败", e);
                response.setImages(new ArrayList<>());
            }
        }
        
        // 设置用户信息
        User creator = userRepository.findById(order.getCreatorId()).orElse(null);
        if (creator != null) {
            OrderResponse.UserInfo creatorInfo = new OrderResponse.UserInfo();
            creatorInfo.setId(creator.getId());
            creatorInfo.setUsername(creator.getUsername());
            creatorInfo.setNickname(creator.getNickname());
            creatorInfo.setAvatarUrl(creator.getAvatarUrl());
            response.setCreator(creatorInfo);
        }
        
        if (order.getReceiverId() != null) {
            User receiver = userRepository.findById(order.getReceiverId()).orElse(null);
            if (receiver != null) {
                OrderResponse.UserInfo receiverInfo = new OrderResponse.UserInfo();
                receiverInfo.setId(receiver.getId());
                receiverInfo.setUsername(receiver.getUsername());
                receiverInfo.setNickname(receiver.getNickname());
                receiverInfo.setAvatarUrl(receiver.getAvatarUrl());
                response.setReceiver(receiverInfo);
            }
        }
        
        // 设置订单项
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdOrderByCreatedTime(order.getId());
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
        
        // 设置评价信息
        List<OrderEvaluation> evaluations = orderEvaluationRepository.findByOrderIdOrderByCreatedTimeDesc(order.getId());
        List<OrderResponse.OrderEvaluationResponse> evaluationResponses = evaluations.stream().map(evaluation -> {
            OrderResponse.OrderEvaluationResponse evalResponse = new OrderResponse.OrderEvaluationResponse();
            evalResponse.setId(evaluation.getId());
            evalResponse.setRating(evaluation.getRating());
            evalResponse.setComment(evaluation.getComment());
            evalResponse.setIsAnonymous(evaluation.getIsAnonymous());
            evalResponse.setCreatedTime(evaluation.getCreatedTime());
            
            // 处理图片、表情和标签
            try {
                if (evaluation.getImages() != null) {
                    evalResponse.setImages(objectMapper.readValue(evaluation.getImages(), List.class));
                }
                if (evaluation.getEmojis() != null) {
                    evalResponse.setEmojis(objectMapper.readValue(evaluation.getEmojis(), List.class));
                }
                if (evaluation.getTags() != null) {
                    evalResponse.setTags(objectMapper.readValue(evaluation.getTags(), List.class));
                }
            } catch (JsonProcessingException e) {
                log.error("反序列化评价数据失败", e);
            }
            
            // 设置评价者信息
            if (!evaluation.getIsAnonymous()) {
                User evaluator = userRepository.findById(evaluation.getEvaluatorId()).orElse(null);
                if (evaluator != null) {
                    OrderResponse.UserInfo evaluatorInfo = new OrderResponse.UserInfo();
                    evaluatorInfo.setId(evaluator.getId());
                    evaluatorInfo.setUsername(evaluator.getUsername());
                    evaluatorInfo.setNickname(evaluator.getNickname());
                    evaluatorInfo.setAvatarUrl(evaluator.getAvatarUrl());
                    evalResponse.setEvaluator(evaluatorInfo);
                }
            }
            
            return evalResponse;
        }).collect(Collectors.toList());
        response.setEvaluations(evaluationResponses);
        
        return response;
    }
}