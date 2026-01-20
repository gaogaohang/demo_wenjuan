package com.couple.platform.controller;

import com.couple.platform.dto.order.*;
import com.couple.platform.service.OrderService;
import com.couple.platform.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "订单管理", description = "订单相关接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ApiResponse.success("订单创建成功", response);
    }

    @Operation(summary = "获取我的订单列表")
    @GetMapping
    public ApiResponse<List<OrderResponse>> getUserOrders(
            @Parameter(description = "订单状态") @RequestParam(required = false) String status) {
        List<OrderResponse> orders = orderService.getUserOrders(status);
        return ApiResponse.success(orders);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        OrderResponse response = orderService.getOrderDetail(id);
        return ApiResponse.success(response);
    }

    @Operation(summary = "更新订单状态")
    @PutMapping("/{id}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request);
        return ApiResponse.success("订单状态更新成功", response);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        OrderResponse response = orderService.cancelOrder(id);
        return ApiResponse.success("订单取消成功", response);
    }

    @Operation(summary = "评价订单")
    @PostMapping("/{id}/evaluate")
    public ApiResponse<OrderResponse> evaluateOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Valid @RequestBody OrderEvaluationRequest request) {
        OrderResponse response = orderService.evaluateOrder(id, request);
        return ApiResponse.success("订单评价成功", response);
    }

    @Operation(summary = "获取订单评价列表")
    @GetMapping("/{id}/evaluations")
    public ApiResponse<List<OrderResponse.OrderEvaluationResponse>> getOrderEvaluations(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        List<OrderResponse.OrderEvaluationResponse> evaluations = orderService.getOrderEvaluations(id);
        return ApiResponse.success(evaluations);
    }
}
