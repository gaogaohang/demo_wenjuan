package com.couple.platform.utils;

import com.couple.platform.security.JwtAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 安全工具类 - 获取当前登录用户信息
 */
@Component
public class SecurityUtil {
    
    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }
    
    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            JwtAuthenticationFilter.JwtAuthenticationDetails details = 
                (JwtAuthenticationFilter.JwtAuthenticationDetails) authentication.getDetails();
            return details.getUsername();
        }
        return null;
    }
    
    /**
     * 获取当前用户类型
     */
    public static String getCurrentUserType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            JwtAuthenticationFilter.JwtAuthenticationDetails details = 
                (JwtAuthenticationFilter.JwtAuthenticationDetails) authentication.getDetails();
            return details.getUserType();
        }
        return null;
    }
    
    /**
     * 检查当前用户是否为管理员
     */
    public static boolean isCurrentUserAdmin() {
        return "admin".equals(getCurrentUserType());
    }
    
    /**
     * 检查当前用户是否为普通用户
     */
    public static boolean isCurrentUserRegular() {
        return "user".equals(getCurrentUserType());
    }
    
    /**
     * 检查是否已登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && getCurrentUserId() != null;
    }
    
    /**
     * 检查当前用户是否有指定权限
     */
    public static boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
        }
        return false;
    }
    
    /**
     * 检查当前用户是否有指定角色
     */
    public static boolean hasRole(String role) {
        return hasAuthority("ROLE_" + role.toUpperCase());
    }
}