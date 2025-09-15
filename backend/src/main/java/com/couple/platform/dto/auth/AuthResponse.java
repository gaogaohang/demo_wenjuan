package com.couple.platform.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "认证响应")
public class AuthResponse {
    
    @Schema(description = "访问令牌")
    private String accessToken;
    
    @Schema(description = "刷新令牌")
    private String refreshToken;
    
    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "过期时间（秒）", example = "86400")
    private Long expiresIn;
    
    @Schema(description = "用户信息")
    private UserInfo userInfo;
    
    public AuthResponse(String accessToken, String refreshToken, Long expiresIn, UserInfo userInfo) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.userInfo = userInfo;
    }
    
    @Data
    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID")
        private Long id;
        
        @Schema(description = "用户名")
        private String username;
        
        @Schema(description = "昵称")
        private String nickname;
        
        @Schema(description = "头像URL")
        private String avatarUrl;
        
        @Schema(description = "手机号")
        private String phone;
        
        @Schema(description = "性别：0-未知，1-男，2-女")
        private Integer gender;
        
        @Schema(description = "是否已配对")
        private Boolean isPaired;
        
        @Schema(description = "配对码")
        private String pairCode;
        
        @Schema(description = "配对对象ID")
        private Long partnerId;
        
        @Schema(description = "用户类型", example = "user")
        private String userType;
    }
}