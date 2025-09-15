package com.couple.platform.controller;

import com.couple.platform.dto.user.*;
import com.couple.platform.entity.UserSettings;
import com.couple.platform.service.UserService;
import com.couple.platform.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "用户相关操作接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    
    private final UserService userService;
    
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/profile")
    public ApiResponse<UserInfoResponse> getCurrentUserInfo() {
        UserInfoResponse response = userService.getCurrentUserInfo();
        return ApiResponse.success(response);
    }
    
    @Operation(summary = "更新用户资料", description = "更新当前用户的个人资料")
    @PutMapping("/profile")
    public ApiResponse<UserInfoResponse> updateUserProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        UserInfoResponse response = userService.updateUserProfile(request);
        return ApiResponse.success("资料更新成功", response);
    }
    
    @Operation(summary = "用户配对", description = "通过配对码与其他用户建立配对关系")
    @PostMapping("/pair")
    public ApiResponse<UserInfoResponse> pairWithUser(@Valid @RequestBody UserPairRequest request) {
        UserInfoResponse response = userService.pairWithUser(request);
        return ApiResponse.success("配对成功", response);
    }
    
    @Operation(summary = "取消配对", description = "取消与当前配对对象的配对关系")
    @PostMapping("/unpair")
    public ApiResponse<UserInfoResponse> unpair() {
        UserInfoResponse response = userService.unpair();
        return ApiResponse.success("取消配对成功", response);
    }
    
    @Operation(summary = "获取用户设置", description = "获取当前用户的个性化设置")
    @GetMapping("/settings")
    public ApiResponse<UserSettings> getUserSettings() {
        UserSettings settings = userService.getUserSettings();
        return ApiResponse.success(settings);
    }
    
    @Operation(summary = "更新用户设置", description = "更新当前用户的个性化设置")
    @PutMapping("/settings")
    public ApiResponse<UserSettings> updateUserSettings(@Valid @RequestBody UserSettingsUpdateRequest request) {
        UserSettings settings = userService.updateUserSettings(request);
        return ApiResponse.success("设置更新成功", settings);
    }
}