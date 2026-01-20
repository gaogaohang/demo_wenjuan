package com.couple.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_evaluations")
public class OrderEvaluation {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("evaluator_id")
    private Long evaluatorId;

    private Integer rating;

    private String comment;

    private String images;

    private String emojis;

    private String tags;

    @TableField("is_anonymous")
    private Boolean isAnonymous;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    public boolean isPositive() {
        return rating != null && rating >= 4;
    }

    public boolean isNegative() {
        return rating != null && rating <= 2;
    }
}
