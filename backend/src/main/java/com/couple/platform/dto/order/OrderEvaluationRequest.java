package com.couple.platform.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * 订单评价请求DTO
 */
@Data
@Schema(description = "订单评价请求")
public class OrderEvaluationRequest {
    
    @Schema(description = "评分：1-5分", example = "5")
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1分")
    @Max(value = 5, message = "评分最高为5分")
    private Integer rating;
    
    @Schema(description = "评价内容", example = "服务很好，很满意！")
    @Size(max = 500, message = "评价内容长度不能超过500字符")
    private String comment;
    
    @Schema(description = "评价图片URL列表")
    private List<String> images;
    
    @Schema(description = "表情列表", example = "[\"😋\", \"👍\", \"❤️\"]")
    private List<String> emojis;
    
    @Schema(description = "标签列表", example = "[\"好吃\", \"快速\", \"贴心\"]")
    private List<String> tags;
    
    @Schema(description = "是否匿名评价", example = "false")
    private Boolean isAnonymous = false;
}