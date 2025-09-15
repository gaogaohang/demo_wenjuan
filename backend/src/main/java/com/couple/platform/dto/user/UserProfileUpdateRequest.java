package com.couple.platform.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用户资料更新请求DTO
 */
@Data
@Schema(description = "用户资料更新请求")
public class UserProfileUpdateRequest {
    
    @Schema(description = "昵称", example = "小明")
    @Size(max = 20, message = "昵称长度不能超过20位")
    private String nickname;
    
    @Schema(description = "头像URL")
    private String avatarUrl;
    
    @Schema(description = "性别：0-未知，1-男，2-女", example = "1")
    private Integer gender;
    
    @Schema(description = "生日", example = "1995-01-01")
    private LocalDate birthday;
}