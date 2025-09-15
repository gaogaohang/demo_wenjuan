package com.couple.platform.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户设置更新请求DTO
 */
@Data
@Schema(description = "用户设置更新请求")
public class UserSettingsUpdateRequest {
    
    @Schema(description = "主题模式：light-浅色，dark-深色", example = "light")
    private String themeMode;
    
    @Schema(description = "主色调", example = "#007AFF")
    private String primaryColor;
    
    @Schema(description = "背景色", example = "#FFFFFF")
    private String backgroundColor;
    
    @Schema(description = "背景图片URL")
    private String backgroundImageUrl;
    
    @Schema(description = "是否启用通知", example = "true")
    private Boolean notificationEnabled;
    
    @Schema(description = "是否启用声音", example = "true")
    private Boolean soundEnabled;
    
    @Schema(description = "是否启用震动", example = "true")
    private Boolean vibrationEnabled;
    
    @Schema(description = "语言设置", example = "zh_CN")
    private String language;
    
    @Schema(description = "时区设置", example = "Asia/Shanghai")
    private String timezone;
}