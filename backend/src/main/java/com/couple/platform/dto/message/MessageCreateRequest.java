package com.couple.platform.dto.message;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class MessageCreateRequest {

    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;

    @NotBlank(message = "消息类型不能为空")
    private String type;

    @NotBlank(message = "消息标题不能为空")
    private String title;

    private String content;

    private String data;
}
