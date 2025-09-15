package com.couple.platform.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求DTO
 */
@Data
@Schema(description = "用户登录请求")
public class UserLoginRequest {
    
    @Schema(description = "手机号或用户名", example = "13800138001")
    @NotBlank(message = "手机号或用户名不能为空")
    private String account;
    
    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @Schema(description = "记住我", example = "true")
    private Boolean rememberMe = false;
}