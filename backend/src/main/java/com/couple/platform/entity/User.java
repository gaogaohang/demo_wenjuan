package com.couple.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@Entity
@Table(name = "users")
@EqualsAndHashCode(callSuper = false)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 20)
    private String phone;
    
    @Column(unique = true, length = 50)
    private String username;
    
    @Column(nullable = false, length = 100)
    private String password;
    
    @Column(length = 50)
    private String nickname;
    
    @Column(name = "avatar_url")
    private String avatarUrl;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer gender;
    
    private LocalDate birthday;
    
    /**
     * 状态：0-禁用，1-正常
     */
    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;
    
    /**
     * 是否已配对
     */
    @Column(name = "is_paired", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPaired;
    
    /**
     * 配对对象ID
     */
    @Column(name = "partner_id")
    private Long partnerId;
    
    /**
     * 配对码
     */
    @Column(name = "pair_code", unique = true, length = 20)
    private String pairCode;
    
    /**
     * 配对时间
     */
    @Column(name = "pair_date")
    private LocalDateTime pairDate;
    
    /**
     * 微信OpenID
     */
    @Column(name = "wechat_openid", length = 100)
    private String wechatOpenid;
    
    /**
     * 微信UnionID
     */
    @Column(name = "wechat_unionid", length = 100)
    private String wechatUnionid;
    
    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登录IP
     */
    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;
    
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
     * 配对对象
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", insertable = false, updatable = false)
    private User partner;
    
    /**
     * 用户设置
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserSettings userSettings;
    
    // 辅助方法
    
    /**
     * 是否为男性
     */
    public boolean isMale() {
        return Integer.valueOf(1).equals(gender);
    }
    
    /**
     * 是否为女性
     */
    public boolean isFemale() {
        return Integer.valueOf(2).equals(gender);
    }
    
    /**
     * 是否正常状态
     */
    public boolean isActive() {
        return Integer.valueOf(1).equals(status);
    }
    
    /**
     * 是否已配对
     */
    public boolean hasPaired() {
        return Boolean.TRUE.equals(isPaired) && partnerId != null;
    }
}