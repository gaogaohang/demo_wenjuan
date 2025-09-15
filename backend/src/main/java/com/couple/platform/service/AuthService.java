package com.couple.platform.service;

import com.couple.platform.dto.auth.*;
import com.couple.platform.entity.Admin;
import com.couple.platform.entity.User;
import com.couple.platform.entity.UserSettings;
import com.couple.platform.exception.BusinessException;
import com.couple.platform.repository.AdminRepository;
import com.couple.platform.repository.UserRepository;
import com.couple.platform.repository.UserSettingsRepository;
import com.couple.platform.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${couple.jwt.expiration}")
    private Long jwtExpiration;
    
    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final int SMS_CODE_EXPIRE_MINUTES = 5;
    private static final int SMS_CODE_LENGTH = 6;
    
    /**
     * 用户注册
     */
    @Transactional
    public AuthResponse userRegister(UserRegisterRequest request) {
        // 验证短信验证码
        validateSmsCode(request.getPhone(), request.getSmsCode());
        
        // 验证密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        
        // 检查手机号是否已注册
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("该手机号已注册");
        }
        
        // 检查用户名是否已存在
        if (request.getUsername() != null && userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setPhone(request.getPhone());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setGender(request.getGender());
        user.setStatus(1);
        user.setIsPaired(false);
        user.setPairCode(generatePairCode());
        
        userRepository.save(user);
        
        // 创建用户设置
        UserSettings userSettings = new UserSettings();
        userSettings.setUserId(user.getId());
        userSettingsRepository.save(userSettings);
        
        // 删除短信验证码
        deleteSmsCode(request.getPhone());
        
        // 生成令牌
        String accessToken = jwtUtil.generateUserToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername(), "user");
        
        // 构建用户信息
        AuthResponse.UserInfo userInfo = buildUserInfo(user);
        
        log.info("用户注册成功: userId={}, phone={}", user.getId(), user.getPhone());
        
        return new AuthResponse(accessToken, refreshToken, jwtExpiration / 1000, userInfo);
    }
    
    /**
     * 用户登录
     */
    public AuthResponse userLogin(UserLoginRequest request) {
        // 查找用户
        User user = userRepository.findByPhone(request.getAccount())
                .or(() -> userRepository.findByUsername(request.getAccount()))
                .orElseThrow(() -> new BusinessException("用户不存在"));
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        // 检查用户状态
        if (!user.isActive()) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        
        // 生成令牌
        String accessToken = jwtUtil.generateUserToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername(), "user");
        
        // 构建用户信息
        AuthResponse.UserInfo userInfo = buildUserInfo(user);
        
        log.info("用户登录成功: userId={}, account={}", user.getId(), request.getAccount());
        
        return new AuthResponse(accessToken, refreshToken, jwtExpiration / 1000, userInfo);
    }
    
    /**
     * 管理员登录
     */
    public AuthResponse adminLogin(AdminLoginRequest request) {
        // 查找管理员
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("管理员不存在"));
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        // 检查管理员状态
        if (!admin.isActive()) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 更新登录信息
        admin.setLastLoginTime(LocalDateTime.now());
        adminRepository.save(admin);
        
        // 生成令牌
        String accessToken = jwtUtil.generateAdminToken(admin.getId(), admin.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(admin.getId(), admin.getUsername(), "admin");
        
        // 构建管理员信息
        AuthResponse.UserInfo userInfo = buildAdminInfo(admin);
        
        log.info("管理员登录成功: adminId={}, username={}", admin.getId(), admin.getUsername());
        
        return new AuthResponse(accessToken, refreshToken, jwtExpiration / 1000, userInfo);
    }
    
    /**
     * 发送短信验证码
     */
    public void sendSmsCode(SendSmsRequest request) {
        String phone = request.getPhone();
        String type = request.getType();
        
        // 检查发送频率限制
        String rateLimitKey = SMS_CODE_PREFIX + "rate:" + phone;
        if (redisTemplate.hasKey(rateLimitKey)) {
            throw new BusinessException("发送过于频繁，请稍后再试");
        }
        
        // 生成验证码
        String code = generateSmsCode();
        
        // 存储验证码到Redis
        String codeKey = SMS_CODE_PREFIX + phone;
        redisTemplate.opsForValue().set(codeKey, code, SMS_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        // 设置发送频率限制（1分钟内不能重复发送）
        redisTemplate.opsForValue().set(rateLimitKey, "1", 1, TimeUnit.MINUTES);
        
        // TODO: 调用阿里云短信服务发送验证码
        log.info("发送短信验证码: phone={}, code={}, type={}", phone, code, type);
        
        // 开发环境直接记录日志
        log.info("短信验证码已发送: {}", code);
    }
    
    /**
     * 刷新令牌
     */
    public AuthResponse refreshToken(String refreshToken) {
        try {
            String newAccessToken = jwtUtil.refreshToken(refreshToken);
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            String userType = jwtUtil.getUserTypeFromToken(refreshToken);
            
            AuthResponse.UserInfo userInfo;
            if ("user".equals(userType)) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new BusinessException("用户不存在"));
                userInfo = buildUserInfo(user);
            } else {
                Admin admin = adminRepository.findById(userId)
                        .orElseThrow(() -> new BusinessException("管理员不存在"));
                userInfo = buildAdminInfo(admin);
            }
            
            return new AuthResponse(newAccessToken, refreshToken, jwtExpiration / 1000, userInfo);
        } catch (Exception e) {
            throw new BusinessException("刷新令牌失败");
        }
    }
    
    /**
     * 验证短信验证码
     */
    private void validateSmsCode(String phone, String code) {
        String codeKey = SMS_CODE_PREFIX + phone;
        String savedCode = (String) redisTemplate.opsForValue().get(codeKey);
        
        if (savedCode == null) {
            throw new BusinessException("验证码已过期");
        }
        
        if (!savedCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }
    }
    
    /**
     * 删除短信验证码
     */
    private void deleteSmsCode(String phone) {
        String codeKey = SMS_CODE_PREFIX + phone;
        redisTemplate.delete(codeKey);
    }
    
    /**
     * 生成短信验证码
     */
    private String generateSmsCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < SMS_CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    /**
     * 生成配对码
     */
    private String generatePairCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        do {
            code.setLength(0);
            for (int i = 0; i < 8; i++) {
                code.append(chars.charAt(random.nextInt(chars.length())));
            }
        } while (userRepository.existsByPairCode(code.toString()));
        
        return code.toString();
    }
    
    /**
     * 构建用户信息
     */
    private AuthResponse.UserInfo buildUserInfo(User user) {
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setPhone(user.getPhone());
        userInfo.setGender(user.getGender());
        userInfo.setIsPaired(user.getIsPaired());
        userInfo.setPairCode(user.getPairCode());
        userInfo.setPartnerId(user.getPartnerId());
        userInfo.setUserType("user");
        return userInfo;
    }
    
    /**
     * 构建管理员信息
     */
    private AuthResponse.UserInfo buildAdminInfo(Admin admin) {
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
        userInfo.setId(admin.getId());
        userInfo.setUsername(admin.getUsername());
        userInfo.setNickname(admin.getRealName());
        userInfo.setAvatarUrl(admin.getAvatarUrl());
        userInfo.setPhone(admin.getPhone());
        userInfo.setUserType("admin");
        return userInfo;
    }
}