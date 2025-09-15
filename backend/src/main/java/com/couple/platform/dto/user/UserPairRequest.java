package com.couple.platform.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户配对请求DTO
 */
@Data
@Schema(description = "用户配对请求")
public class UserPairRequest {
    
    @Schema(description = "配对码", example = "PAIR001A")
    @NotBlank(message = "配对码不能为空")
    @Pattern(regexp = "^[A-Z0-9]{8}$", message = "配对码格式不正确")
    private String pairCode;
}