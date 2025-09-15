package com.couple.platform.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 发送短信验证码请求DTO
 */
@Data
@Schema(description = "发送短信验证码请求")
public class SendSmsRequest {
    
    @Schema(description = "手机号", example = "13800138001")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Schema(description = "短信类型：register-注册，login-登录，reset-重置密码", example = "register")
    @NotBlank(message = "短信类型不能为空")
    @Pattern(regexp = "^(register|login|reset)$", message = "短信类型必须是register、login或reset")
    private String type;
}