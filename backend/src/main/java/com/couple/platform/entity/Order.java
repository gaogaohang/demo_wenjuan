package com.couple.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("creator_id")
    private Long creatorId;

    @TableField("receiver_id")
    private Long receiverId;

    private String title;

    private String description;

    private String type;

    private String status;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    private String note;

    private String images;

    private String location;

    @TableField("estimated_time")
    private LocalDateTime estimatedTime;

    @TableField("accepted_time")
    private LocalDateTime acceptedTime;

    @TableField("completed_time")
    private LocalDateTime completedTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    public boolean isPending() {
        return "pending".equals(status);
    }

    public boolean isAccepted() {
        return "accepted".equals(status);
    }

    public boolean isProcessing() {
        return "processing".equals(status);
    }

    public boolean isCompleted() {
        return "completed".equals(status);
    }

    public boolean isCancelled() {
        return "cancelled".equals(status);
    }

    public boolean canCancel() {
        return isPending() || isAccepted();
    }

    public boolean canEvaluate() {
        return isCompleted();
    }
}
