package com.couple.platform.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息响应DTO
 */
@Data
@Schema(description = "用户信息响应")
public class UserInfoResponse {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "头像URL")
    private String avatarUrl;
    
    @Schema(description = "性别：0-未知，1-男，2-女")
    private Integer gender;
    
    @Schema(description = "生日")
    private LocalDate birthday;
    
    @Schema(description = "是否已配对")
    private Boolean isPaired;
    
    @Schema(description = "配对码")
    private String pairCode;
    
    @Schema(description = "配对时间")
    private LocalDateTime pairDate;
    
    @Schema(description = "配对对象信息")
    private PartnerInfo partner;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
    
    @Data
    @Schema(description = "配对对象信息")
    public static class PartnerInfo {
        @Schema(description = "用户ID")
        private Long id;
        
        @Schema(description = "用户名")
        private String username;
        
        @Schema(description = "昵称")
        private String nickname;
        
        @Schema(description = "头像URL")
        private String avatarUrl;
        
        @Schema(description = "性别：0-未知，1-男，2-女")
        private Integer gender;
    }
}