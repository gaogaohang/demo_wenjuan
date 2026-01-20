package com.couple.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户设置实体
 */
@Data
@TableName("user_settings")
public class UserSettings {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    /**
     * 主题模式：light-浅色，dark-深色
     */
    @TableField("theme_mode")
    private String themeMode = "light";
    
    /**
     * 主色调
     */
    @TableField("primary_color")
    private String primaryColor = "#007AFF";
    
    /**
     * 背景色
     */
    @TableField("background_color")
    private String backgroundColor = "#FFFFFF";
    
    /**
     * 背景图片URL
     */
    @TableField("background_image_url")
    private String backgroundImageUrl;
    
    /**
     * 是否启用通知
     */
    @TableField("notification_enabled")
    private Boolean notificationEnabled = true;
    
    /**
     * 是否启用声音
     */
    @TableField("sound_enabled")
    private Boolean soundEnabled = true;
    
    /**
     * 是否启用震动
     */
    @TableField("vibration_enabled")
    private Boolean vibrationEnabled = true;
    
    /**
     * 语言设置
     */
    private String language = "zh_CN";
    
    /**
     * 时区设置
     */
    private String timezone = "Asia/Shanghai";
    
    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    private User user;
    
    // 辅助方法
    
    /**
     * 是否深色主题
     */
    public boolean isDarkTheme() {
        return "dark".equals(themeMode);
    }
    
    /**
     * 是否启用全部通知
     */
    public boolean isAllNotificationEnabled() {
        return Boolean.TRUE.equals(notificationEnabled) && 
               Boolean.TRUE.equals(soundEnabled) && 
               Boolean.TRUE.equals(vibrationEnabled);
    }
}