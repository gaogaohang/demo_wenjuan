package com.couple.platform.controller;

import com.couple.platform.dto.auth.*;
import com.couple.platform.service.AuthService;
import com.couple.platform.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户认证相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @Operation(summary = "用户注册", description = "通过手机号注册新用户账号")
    @PostMapping("/register")
    public ApiResponse<AuthResponse> userRegister(@Valid @RequestBody UserRegisterRequest request) {
        AuthResponse response = authService.userRegister(request);
        return ApiResponse.success("注册成功", response);
    }
    
    @Operation(summary = "用户登录", description = "通过手机号或用户名登录")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> userLogin(@Valid @RequestBody UserLoginRequest request) {
        AuthResponse response = authService.userLogin(request);
        return ApiResponse.success("登录成功", response);
    }
    
    @Operation(summary = "管理员登录", description = "管理员账号登录")
    @PostMapping("/admin/login")
    public ApiResponse<AuthResponse> adminLogin(@Valid @RequestBody AdminLoginRequest request) {
        AuthResponse response = authService.adminLogin(request);
        return ApiResponse.success("登录成功", response);
    }
    
    @Operation(summary = "发送短信验证码", description = "发送短信验证码到指定手机号")
    @PostMapping("/sms/send")
    public ApiResponse<Void> sendSmsCode(@Valid @RequestBody SendSmsRequest request) {
        authService.sendSmsCode(request);
        return ApiResponse.success("验证码发送成功");
    }
    
    @Operation(summary = "刷新访问令牌", description = "使用刷新令牌获取新的访问令牌")
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ApiResponse.success("令牌刷新成功", response);
    }
    
    @Operation(summary = "用户登出", description = "用户登出（客户端删除令牌即可）")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        // JWT是无状态的，登出只需要客户端删除令牌
        // 如果需要服务端验证，可以将令牌加入黑名单
        return ApiResponse.success("登出成功");
    }
}