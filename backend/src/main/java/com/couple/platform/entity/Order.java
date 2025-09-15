package com.couple.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体
 */
@Data
@Entity
@Table(name = "orders")
@EqualsAndHashCode(callSuper = false)
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_no", unique = true, nullable = false, length = 32)
    private String orderNo;
    
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;
    
    @Column(name = "receiver_id")
    private Long receiverId;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * 订单类型：food-餐饮，shopping-购物，other-其他
     */
    @Column(length = 20)
    private String type = "food";
    
    /**
     * 订单状态：pending-待处理，accepted-已接受，processing-处理中，completed-已完成，cancelled-已取消
     */
    @Column(length = 20)
    private String status = "pending";
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Column(columnDefinition = "TEXT")
    private String note;
    
    /**
     * 图片列表（JSON格式）
     */
    @Column(columnDefinition = "JSON")
    private String images;
    
    @Column(length = 255)
    private String location;
    
    @Column(name = "estimated_time")
    private LocalDateTime estimatedTime;
    
    @Column(name = "accepted_time")
    private LocalDateTime acceptedTime;
    
    @Column(name = "completed_time")
    private LocalDateTime completedTime;
    
    @CreationTimestamp
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;
    
    @UpdateTimestamp
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    // 关联关系
    
    /**
     * 创建者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", insertable = false, updatable = false)
    private User creator;
    
    /**
     * 接收者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", insertable = false, updatable = false)
    private User receiver;
    
    /**
     * 订单项
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    
    /**
     * 订单评价
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderEvaluation> evaluations;
    
    // 辅助方法
    
    /**
     * 是否待处理
     */
    public boolean isPending() {
        return "pending".equals(status);
    }
    
    /**
     * 是否已接受
     */
    public boolean isAccepted() {
        return "accepted".equals(status);
    }
    
    /**
     * 是否处理中
     */
    public boolean isProcessing() {
        return "processing".equals(status);
    }
    
    /**
     * 是否已完成
     */
    public boolean isCompleted() {
        return "completed".equals(status);
    }
    
    /**
     * 是否已取消
     */
    public boolean isCancelled() {
        return "cancelled".equals(status);
    }
    
    /**
     * 是否可以取消
     */
    public boolean canCancel() {
        return isPending() || isAccepted();
    }
    
    /**
     * 是否可以评价
     */
    public boolean canEvaluate() {
        return isCompleted();
    }
}