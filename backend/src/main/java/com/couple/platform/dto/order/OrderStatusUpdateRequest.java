package com.couple.platform.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 订单状态更新请求DTO
 */
@Data
@Schema(description = "订单状态更新请求")
public class OrderStatusUpdateRequest {
    
    @Schema(description = "订单状态：pending-待处理，accepted-已接受，processing-处理中，completed-已完成，cancelled-已取消", 
            example = "accepted")
    @NotBlank(message = "订单状态不能为空")
    @Pattern(regexp = "^(pending|accepted|processing|completed|cancelled)$", 
             message = "订单状态必须是pending、accepted、processing、completed或cancelled")
    private String status;
    
    @Schema(description = "状态更新说明", example = "已接受订单，正在处理中")
    private String message;
}