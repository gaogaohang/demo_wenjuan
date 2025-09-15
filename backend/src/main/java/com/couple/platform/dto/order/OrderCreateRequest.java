package com.couple.platform.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建订单请求DTO
 */
@Data
@Schema(description = "创建订单请求")
public class OrderCreateRequest {
    
    @Schema(description = "订单标题", example = "晚餐订单")
    @NotBlank(message = "订单标题不能为空")
    @Size(max = 100, message = "订单标题长度不能超过100字符")
    private String title;
    
    @Schema(description = "订单描述", example = "今晚想吃火锅，帮我点一下吧~")
    @Size(max = 500, message = "订单描述长度不能超过500字符")
    private String description;
    
    @Schema(description = "订单类型：food-餐饮，shopping-购物，other-其他", example = "food")
    private String type = "food";
    
    @Schema(description = "备注信息", example = "微辣，不要香菜")
    @Size(max = 200, message = "备注信息长度不能超过200字符")
    private String note;
    
    @Schema(description = "图片URL列表")
    private List<String> images;
    
    @Schema(description = "地址位置", example = "北京市朝阳区")
    @Size(max = 255, message = "地址位置长度不能超过255字符")
    private String location;
    
    @Schema(description = "预计完成时间")
    private LocalDateTime estimatedTime;
    
    @Schema(description = "订单项列表")
    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemRequest> items;
    
    @Data
    @Schema(description = "订单项请求")
    public static class OrderItemRequest {
        @Schema(description = "商品名称", example = "番茄火锅底料")
        @NotBlank(message = "商品名称不能为空")
        @Size(max = 100, message = "商品名称长度不能超过100字符")
        private String name;
        
        @Schema(description = "商品描述", example = "微辣番茄锅底")
        @Size(max = 200, message = "商品描述长度不能超过200字符")
        private String description;
        
        @Schema(description = "数量", example = "1")
        private Integer quantity = 1;
        
        @Schema(description = "单价", example = "28.00")
        private BigDecimal unitPrice = BigDecimal.ZERO;
        
        @Schema(description = "商品图片URL")
        private String imageUrl;
        
        @Schema(description = "备注", example = "微辣")
        @Size(max = 100, message = "备注长度不能超过100字符")
        private String note;
    }
}