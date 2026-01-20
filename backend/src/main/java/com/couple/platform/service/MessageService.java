package com.couple.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.couple.platform.dto.message.MessageCreateRequest;
import com.couple.platform.dto.message.MessageResponse;
import com.couple.platform.entity.Message;
import com.couple.platform.enums.ErrorCode;
import com.couple.platform.exception.BusinessException;
import com.couple.platform.mapper.MessageMapper;
import com.couple.platform.mapper.UserMapper;
import com.couple.platform.utils.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService extends ServiceImpl<MessageMapper, Message> {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    public Long createMessage(MessageCreateRequest request) {
        Long senderId = SecurityUtil.getCurrentUserId();

        if (senderId.equals(request.getReceiverId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能给自己发送消息");
        }

        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setType(request.getType());
        message.setTitle(request.getTitle());
        message.setContent(request.getContent());
        message.setData(request.getData());
        message.setIsRead(false);

        messageMapper.insert(message);

        log.info("消息创建成功: messageId={}, senderId={}, receiverId={}, type={}",
                message.getId(), senderId, request.getReceiverId(), request.getType());

        return message.getId();
    }

    public List<MessageResponse> getMyMessages(Boolean isRead, String type) {
        Long userId = SecurityUtil.getCurrentUserId();

        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .eq(Message::getReceiverId, userId)
                .orderByDesc(Message::getCreatedTime);

        if (isRead != null) {
            wrapper.eq(Message::getIsRead, isRead);
        }

        if (type != null && !type.isEmpty()) {
            wrapper.eq(Message::getType, type);
        }

        List<Message> messages = messageMapper.selectList(wrapper);

        return messages.stream()
                .map(this::buildMessageResponse)
                .collect(Collectors.toList());
    }

    public MessageResponse getMessageDetail(Long messageId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException(ErrorCode.MESSAGE_NOT_FOUND);
        }

        if (!message.getReceiverId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看此消息");
        }

        return buildMessageResponse(message);
    }

    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long messageId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException(ErrorCode.MESSAGE_NOT_FOUND);
        }

        if (!message.getReceiverId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此消息");
        }

        if (Boolean.TRUE.equals(message.getIsRead())) {
            return;
        }

        message.setIsRead(true);
        message.setReadTime(LocalDateTime.now());
        messageMapper.updateById(message);

        log.info("消息标记为已读: messageId={}, userId={}", messageId, userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead() {
        Long userId = SecurityUtil.getCurrentUserId();

        messageMapper.update(null,
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getReceiverId, userId)
                        .eq(Message::getIsRead, false)
        );

        log.info("所有消息标记为已读: userId={}", userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(Long messageId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException(ErrorCode.MESSAGE_NOT_FOUND);
        }

        if (!message.getReceiverId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除此消息");
        }

        messageMapper.deleteById(messageId);

        log.info("消息删除成功: messageId={}, userId={}", messageId, userId);
    }

    public Long getUnreadCount() {
        Long userId = SecurityUtil.getCurrentUserId();

        Long count = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getReceiverId, userId)
                        .eq(Message::getIsRead, false)
        );

        return count;
    }

    private MessageResponse buildMessageResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setType(message.getType());
        response.setTitle(message.getTitle());
        response.setContent(message.getContent());
        response.setData(message.getData());
        response.setIsRead(message.getIsRead());
        response.setReadTime(message.getReadTime());
        response.setCreatedTime(message.getCreatedTime());

        if (message.getSenderId() != null) {
            var sender = userMapper.selectById(message.getSenderId());
            if (sender != null) {
                response.setSenderUsername(sender.getUsername());
                response.setSenderNickname(sender.getNickname());
                response.setSenderAvatar(sender.getAvatarUrl());
            }
        }

        return response;
    }

    public void sendSystemMessage(Long receiverId, String title, String content, String data) {
        Message message = new Message();
        message.setSenderId(null);
        message.setReceiverId(receiverId);
        message.setType("system");
        message.setTitle(title);
        message.setContent(content);
        message.setData(data);
        message.setIsRead(false);

        messageMapper.insert(message);

        log.info("系统消息发送成功: messageId={}, receiverId={}, title={}",
                message.getId(), receiverId, title);
    }

    public void sendOrderMessage(Long receiverId, String title, String content, Long orderId) {
        try {
            String data = objectMapper.writeValueAsString(orderId);
            sendSystemMessage(receiverId, title, content, data);
        } catch (JsonProcessingException e) {
            log.error("序列化订单数据失败", e);
        }
    }

    public void sendSurveyMessage(Long receiverId, String title, String content, Long surveyId) {
        try {
            String data = objectMapper.writeValueAsString(surveyId);
            sendSystemMessage(receiverId, title, content, data);
        } catch (JsonProcessingException e) {
            log.error("序列化问卷数据失败", e);
        }
    }
}
