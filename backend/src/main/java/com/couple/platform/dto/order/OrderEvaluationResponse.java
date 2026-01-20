package com.couple.platform.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "订单评价响应")
public class OrderEvaluationResponse {

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

    @Schema(description = "评价者信息")
    private OrderResponse.UserInfo evaluator;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
}
