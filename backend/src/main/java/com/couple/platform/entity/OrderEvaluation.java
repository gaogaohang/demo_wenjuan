package com.couple.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 订单评价实体
 */
@Data
@Entity
@Table(name = "order_evaluations")
@EqualsAndHashCode(callSuper = false)
public class OrderEvaluation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    @Column(name = "evaluator_id", nullable = false)
    private Long evaluatorId;
    
    /**
     * 评分：1-5分
     */
    @Column(nullable = false, columnDefinition = "TINYINT")
    private Integer rating;
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    /**
     * 评价图片（JSON格式）
     */
    @Column(columnDefinition = "JSON")
    private String images;
    
    /**
     * 表情列表（JSON格式）
     */
    @Column(columnDefinition = "JSON")
    private String emojis;
    
    /**
     * 标签列表（JSON格式）
     */
    @Column(columnDefinition = "JSON")
    private String tags;
    
    /**
     * 是否匿名评价
     */
    @Column(name = "is_anonymous", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isAnonymous = false;
    
    @CreationTimestamp
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;
    
    // 关联关系
    
    /**
     * 所属订单
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;
    
    /**
     * 评价者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", insertable = false, updatable = false)
    private User evaluator;
    
    // 辅助方法
    
    /**
     * 是否好评
     */
    public boolean isPositive() {
        return rating != null && rating >= 4;
    }
    
    /**
     * 是否差评
     */
    public boolean isNegative() {
        return rating != null && rating <= 2;
    }
}