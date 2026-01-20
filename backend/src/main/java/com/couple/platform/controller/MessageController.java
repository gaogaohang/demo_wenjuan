package com.couple.platform.controller;

import com.couple.platform.dto.message.MessageCreateRequest;
import com.couple.platform.dto.message.MessageResponse;
import com.couple.platform.service.MessageService;
import com.couple.platform.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "消息管理", description = "消息相关接口")
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "发送消息")
    @PostMapping
    public ApiResponse<Long> createMessage(@Valid @RequestBody MessageCreateRequest request) {
        Long messageId = messageService.createMessage(request);
        return ApiResponse.success("消息发送成功", messageId);
    }

    @Operation(summary = "获取我的消息列表")
    @GetMapping
    public ApiResponse<List<MessageResponse>> getMyMessages(
            @Parameter(description = "是否已读") @RequestParam(required = false) Boolean isRead,
            @Parameter(description = "消息类型") @RequestParam(required = false) String type) {
        List<MessageResponse> messages = messageService.getMyMessages(isRead, type);
        return ApiResponse.success(messages);
    }

    @Operation(summary = "获取消息详情")
    @GetMapping("/{id}")
    public ApiResponse<MessageResponse> getMessageDetail(
            @Parameter(description = "消息ID") @PathVariable Long id) {
        MessageResponse message = messageService.getMessageDetail(id);
        return ApiResponse.success(message);
    }

    @Operation(summary = "标记消息为已读")
    @PostMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(
            @Parameter(description = "消息ID") @PathVariable Long id) {
        messageService.markAsRead(id);
        return ApiResponse.success("消息已标记为已读", null);
    }

    @Operation(summary = "标记所有消息为已读")
    @PostMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        messageService.markAllAsRead();
        return ApiResponse.success("所有消息已标记为已读", null);
    }

    @Operation(summary = "删除消息")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMessage(
            @Parameter(description = "消息ID") @PathVariable Long id) {
        messageService.deleteMessage(id);
        return ApiResponse.success("消息删除成功", null);
    }

    @Operation(summary = "获取未读消息数量")
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        Long count = messageService.getUnreadCount();
        return ApiResponse.success(count);
    }
}
