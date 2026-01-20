package com.couple.platform.service;

import com.couple.platform.dto.user.*;
import com.couple.platform.entity.User;
import com.couple.platform.entity.UserSettings;
import com.couple.platform.enums.ErrorCode;
import com.couple.platform.exception.BusinessException;
import com.couple.platform.mapper.UserMapper;
import com.couple.platform.mapper.UserSettingsMapper;
import com.couple.platform.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final UserSettingsMapper userSettingsMapper;
    
    /**
     * 获取当前用户信息
     */
    public UserInfoResponse getCurrentUserInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        return buildUserInfoResponse(user);
    }
    
    /**
     * 更新用户资料
     */
    @Transactional
    public UserInfoResponse updateUserProfile(UserProfileUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 更新用户资料
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }
        
        userMapper.updateById(user);
        
        log.info("用户资料更新成功: userId={}", userId);
        
        return buildUserInfoResponse(user);
    }
    
    /**
     * 用户配对
     */
    @Transactional
    public UserInfoResponse pairWithUser(UserPairRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 检查当前用户是否已配对
        if (currentUser.hasPaired()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_PAIRED, "您已经配对过了");
        }
        
        // 查找配对目标用户
        User targetUser = userMapper.findByPairCode(request.getPairCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.PAIR_CODE_INVALID, "配对码不存在"));
        
        // 检查是否是自己的配对码
        if (targetUser.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能与自己配对");
        }
        
        // 检查目标用户是否已配对
        if (targetUser.hasPaired()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_PAIRED, "对方已经配对过了");
        }
        
        // 检查目标用户状态
        if (!targetUser.isActive()) {
            throw new BusinessException(ErrorCode.USER_DISABLED, "目标用户状态异常");
        }
        
        // 执行配对
        LocalDateTime now = LocalDateTime.now();
        
        currentUser.setIsPaired(true);
        currentUser.setPartnerId(targetUser.getId());
        currentUser.setPairDate(now);
        
        targetUser.setIsPaired(true);
        targetUser.setPartnerId(currentUser.getId());
        targetUser.setPairDate(now);
        
        userMapper.updateById(currentUser);
        userMapper.updateById(targetUser);
        
        log.info("用户配对成功: userId1={}, userId2={}", userId, targetUser.getId());
        
        return buildUserInfoResponse(currentUser);
    }
    
    /**
     * 取消配对
     */
    @Transactional
    public UserInfoResponse unpair() {
        Long userId = SecurityUtil.getCurrentUserId();
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 检查是否已配对
        if (!currentUser.hasPaired()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "您还没有配对");
        }
        
        // 查找配对对象
        User partner = userMapper.selectById(currentUser.getPartnerId());
        
        // 取消当前用户的配对
        currentUser.setIsPaired(false);
        currentUser.setPartnerId(null);
        currentUser.setPairDate(null);
        userMapper.updateById(currentUser);
        
        // 取消配对对象的配对
        if (partner != null) {
            partner.setIsPaired(false);
            partner.setPartnerId(null);
            partner.setPairDate(null);
            userMapper.updateById(partner);
        }
        
        log.info("用户取消配对成功: userId={}, partnerId={}", userId, partner != null ? partner.getId() : null);
        
        return buildUserInfoResponse(currentUser);
    }
    
    /**
     * 获取用户设置
     */
    public UserSettings getUserSettings() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userSettingsMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserSettings>()
                        .eq("user_id", userId)
        );
    }
    
    /**
     * 更新用户设置
     */
    @Transactional
    public UserSettings updateUserSettings(UserSettingsUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        UserSettings settings = userSettingsMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserSettings>()
                        .eq("user_id", userId)
        );
        if (settings == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户设置不存在");
        }
        
        // 更新设置
        if (request.getThemeMode() != null) {
            settings.setThemeMode(request.getThemeMode());
        }
        if (request.getPrimaryColor() != null) {
            settings.setPrimaryColor(request.getPrimaryColor());
        }
        if (request.getBackgroundColor() != null) {
            settings.setBackgroundColor(request.getBackgroundColor());
        }
        if (request.getBackgroundImageUrl() != null) {
            settings.setBackgroundImageUrl(request.getBackgroundImageUrl());
        }
        if (request.getNotificationEnabled() != null) {
            settings.setNotificationEnabled(request.getNotificationEnabled());
        }
        if (request.getSoundEnabled() != null) {
            settings.setSoundEnabled(request.getSoundEnabled());
        }
        if (request.getVibrationEnabled() != null) {
            settings.setVibrationEnabled(request.getVibrationEnabled());
        }
        if (request.getLanguage() != null) {
            settings.setLanguage(request.getLanguage());
        }
        if (request.getTimezone() != null) {
            settings.setTimezone(request.getTimezone());
        }
        
        userSettingsMapper.updateById(settings);
        
        log.info("用户设置更新成功: userId={}", userId);
        
        return settings;
    }
    
    /**
     * 构建用户信息响应
     */
    private UserInfoResponse buildUserInfoResponse(User user) {
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setPhone(user.getPhone());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setGender(user.getGender());
        response.setBirthday(user.getBirthday());
        response.setIsPaired(user.getIsPaired());
        response.setPairCode(user.getPairCode());
        response.setPairDate(user.getPairDate());
        response.setCreatedTime(user.getCreatedTime());
        
        // 设置配对对象信息
        if (user.hasPaired()) {
            User partner = userMapper.selectById(user.getPartnerId());
            if (partner != null) {
                UserInfoResponse.PartnerInfo partnerInfo = new UserInfoResponse.PartnerInfo();
                partnerInfo.setId(partner.getId());
                partnerInfo.setUsername(partner.getUsername());
                partnerInfo.setNickname(partner.getNickname());
                partnerInfo.setAvatarUrl(partner.getAvatarUrl());
                partnerInfo.setGender(partner.getGender());
                response.setPartner(partnerInfo);
            }
        }
        
        return response;
    }
}