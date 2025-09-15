package com.couple.platform.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应DTO
 */
@Data
@Schema(description = "订单响应")
public class OrderResponse {
    
    @Schema(description = "订单ID")
    private Long id;
    
    @Schema(description = "订单号")
    private String orderNo;
    
    @Schema(description = "订单标题")
    private String title;
    
    @Schema(description = "订单描述")
    private String description;
    
    @Schema(description = "订单类型：food-餐饮，shopping-购物，other-其他")
    private String type;
    
    @Schema(description = "订单状态：pending-待处理，accepted-已接受，processing-处理中，completed-已完成，cancelled-已取消")
    private String status;
    
    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;
    
    @Schema(description = "备注信息")
    private String note;
    
    @Schema(description = "图片URL列表")
    private List<String> images;
    
    @Schema(description = "地址位置")
    private String location;
    
    @Schema(description = "预计完成时间")
    private LocalDateTime estimatedTime;
    
    @Schema(description = "接受时间")
    private LocalDateTime acceptedTime;
    
    @Schema(description = "完成时间")
    private LocalDateTime completedTime;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
    
    @Schema(description = "创建者信息")
    private UserInfo creator;
    
    @Schema(description = "接收者信息")
    private UserInfo receiver;
    
    @Schema(description = "订单项列表")
    private List<OrderItemResponse> items;
    
    @Schema(description = "评价列表")
    private List<OrderEvaluationResponse> evaluations;
    
    @Data
    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID")
        private Long id;
        
        @Schema(description = "用户名")
        private String username;
        
        @Schema(description = "昵称")
        private String nickname;
        
        @Schema(description = "头像URL")
        private String avatarUrl;
    }
    
    @Data
    @Schema(description = "订单项响应")
    public static class OrderItemResponse {
        @Schema(description = "订单项ID")
        private Long id;
        
        @Schema(description = "商品名称")
        private String name;
        
        @Schema(description = "商品描述")
        private String description;
        
        @Schema(description = "数量")
        private Integer quantity;
        
        @Schema(description = "单价")
        private BigDecimal unitPrice;
        
        @Schema(description = "总价")
        private BigDecimal totalPrice;
        
        @Schema(description = "商品图片URL")
        private String imageUrl;
        
        @Schema(description = "备注")
        private String note;
    }
    
    @Data
    @Schema(description = "订单评价响应")
    public static class OrderEvaluationResponse {
        @Schema(description = "评价ID")
        private Long id;
        
        @Schema(description = "评分：1-5分")
        private Integer rating;
        
        @Schema(description = "评价内容")
        private String comment;
        
        @Schema(description = "评价图片URL列表")
        private List<String> images;
        
        @Schema(description = "表情列表")
        private List<String> emojis;
        
        @Schema(description = "标签列表")
        private List<String> tags;
        
        @Schema(description = "是否匿名评价")
        private Boolean isAnonymous;
        
        @Schema(description = "评价时间")
        private LocalDateTime createdTime;
        
        @Schema(description = "评价者信息")
        private UserInfo evaluator;
    }
}