package com.couple.platform.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理员登录请求DTO
 */
@Data
@Schema(description = "管理员登录请求")
public class AdminLoginRequest {
    
    @Schema(description = "用户名", example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @Schema(description = "密码", example = "admin123456")
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @Schema(description = "记住我", example = "true")
    private Boolean rememberMe = false;
}