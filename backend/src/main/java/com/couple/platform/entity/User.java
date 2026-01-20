package com.couple.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String phone;

    private String username;

    private String password;

    private String nickname;

    @TableField("avatar_url")
    private String avatarUrl;

    private Integer gender;

    private LocalDate birthday;

    private Integer status;

    @TableField("is_paired")
    private Boolean isPaired;

    @TableField("partner_id")
    private Long partnerId;

    @TableField("pair_code")
    private String pairCode;

    @TableField("pair_date")
    private LocalDateTime pairDate;

    @TableField("wechat_openid")
    private String wechatOpenid;

    @TableField("wechat_unionid")
    private String wechatUnionid;

    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField("last_login_ip")
    private String lastLoginIp;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    public boolean isMale() {
        return Integer.valueOf(1).equals(gender);
    }

    public boolean isFemale() {
        return Integer.valueOf(2).equals(gender);
    }

    public boolean isActive() {
        return Integer.valueOf(1).equals(status);
    }

    public boolean hasPaired() {
        return Boolean.TRUE.equals(isPaired) && partnerId != null;
    }
}