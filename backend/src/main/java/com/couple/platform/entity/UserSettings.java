package com.couple.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户设置实体
 */
@Data
@Entity
@Table(name = "user_settings")
@EqualsAndHashCode(callSuper = false)
public class UserSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 主题模式：light-浅色，dark-深色
     */
    @Column(name = "theme_mode", length = 20)
    private String themeMode = "light";
    
    /**
     * 主色调
     */
    @Column(name = "primary_color", length = 10)
    private String primaryColor = "#007AFF";
    
    /**
     * 背景色
     */
    @Column(name = "background_color", length = 10)
    private String backgroundColor = "#FFFFFF";
    
    /**
     * 背景图片URL
     */
    @Column(name = "background_image_url")
    private String backgroundImageUrl;
    
    /**
     * 是否启用通知
     */
    @Column(name = "notification_enabled", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean notificationEnabled = true;
    
    /**
     * 是否启用声音
     */
    @Column(name = "sound_enabled", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean soundEnabled = true;
    
    /**
     * 是否启用震动
     */
    @Column(name = "vibration_enabled", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean vibrationEnabled = true;
    
    /**
     * 语言设置
     */
    @Column(length = 10)
    private String language = "zh_CN";
    
    /**
     * 时区设置
     */
    @Column(length = 50)
    private String timezone = "Asia/Shanghai";
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    // 关联关系
    
    /**
     * 用户
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
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