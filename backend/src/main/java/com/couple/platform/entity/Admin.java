package com.couple.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 管理员实体
 */
@Data
@Entity
@Table(name = "admins")
@EqualsAndHashCode(callSuper = false)
public class Admin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(nullable = false, length = 100)
    private String password;
    
    @Column(name = "real_name", length = 50)
    private String realName;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(name = "avatar_url")
    private String avatarUrl;
    
    /**
     * 角色：super_admin-超级管理员，admin-管理员
     */
    @Column(length = 20)
    private String role = "admin";
    
    /**
     * 状态：0-禁用，1-正常
     */
    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;
    
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
    
    // 辅助方法
    
    /**
     * 是否超级管理员
     */
    public boolean isSuperAdmin() {
        return "super_admin".equals(role);
    }
    
    /**
     * 是否正常状态
     */
    public boolean isActive() {
        return Integer.valueOf(1).equals(status);
    }
}