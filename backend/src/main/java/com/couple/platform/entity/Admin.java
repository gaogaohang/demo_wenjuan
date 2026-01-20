package com.couple.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员实体
 */
@Data
@TableName("admins")
public class Admin {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    @TableField("real_name")
    private String realName;
    
    private String email;
    
    private String phone;
    
    @TableField("avatar_url")
    private String avatarUrl;
    
    /**
     * 角色：super_admin-超级管理员，admin-管理员
     */
    private String role = "admin";
    
    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status = 1;
    
    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登录IP
     */
    @TableField("last_login_ip")
    private String lastLoginIp;
    
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