package com.couple.platform.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {
    
    @Value("${couple.jwt.secret}")
    private String secret;
    
    @Value("${couple.jwt.expiration}")
    private Long expiration;
    
    @Value("${couple.jwt.refresh-expiration}")
    private Long refreshExpiration;
    
    private static final String ISSUER = "couple-platform";
    private static final String USER_TYPE_CLAIM = "userType";
    private static final String USER_ID_CLAIM = "userId";
    private static final String USERNAME_CLAIM = "username";
    
    /**
     * 生成用户访问Token
     */
    public String generateUserToken(Long userId, String username) {
        return generateToken(userId, username, "user", expiration);
    }
    
    /**
     * 生成管理员访问Token
     */
    public String generateAdminToken(Long adminId, String username) {
        return generateToken(adminId, username, "admin", expiration);
    }
    
    /**
     * 生成刷新Token
     */
    public String generateRefreshToken(Long userId, String username, String userType) {
        return generateToken(userId, username, userType, refreshExpiration);
    }
    
    /**
     * 生成Token
     */
    private String generateToken(Long userId, String username, String userType, Long expirationTime) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Date now = new Date();
            Date expireTime = new Date(now.getTime() + expirationTime);
            
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(String.valueOf(userId))
                    .withClaim(USER_ID_CLAIM, userId)
                    .withClaim(USERNAME_CLAIM, username)
                    .withClaim(USER_TYPE_CLAIM, userType)
                    .withIssuedAt(now)
                    .withExpiresAt(expireTime)
                    .sign(algorithm);
        } catch (Exception e) {
            log.error("生成Token失败", e);
            throw new RuntimeException("生成Token失败", e);
        }
    }
    
    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        try {
            getDecodedJWT(token);
            return true;
        } catch (Exception e) {
            log.warn("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            DecodedJWT jwt = getDecodedJWT(token);
            return jwt.getClaim(USER_ID_CLAIM).asLong();
        } catch (Exception e) {
            log.error("从Token获取用户ID失败", e);
            throw new RuntimeException("从Token获取用户ID失败", e);
        }
    }
    
    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            DecodedJWT jwt = getDecodedJWT(token);
            return jwt.getClaim(USERNAME_CLAIM).asString();
        } catch (Exception e) {
            log.error("从Token获取用户名失败", e);
            throw new RuntimeException("从Token获取用户名失败", e);
        }
    }
    
    /**
     * 从Token中获取用户类型
     */
    public String getUserTypeFromToken(String token) {
        try {
            DecodedJWT jwt = getDecodedJWT(token);
            return jwt.getClaim(USER_TYPE_CLAIM).asString();
        } catch (Exception e) {
            log.error("从Token获取用户类型失败", e);
            throw new RuntimeException("从Token获取用户类型失败", e);
        }
    }
    
    /**
     * 检查Token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = getDecodedJWT(token);
            return jwt.getExpiresAt().before(new Date());
        } catch (Exception e) {
            log.warn("检查Token过期状态失败: {}", e.getMessage());
            return true;
        }
    }
    
    /**
     * 获取Token过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            DecodedJWT jwt = getDecodedJWT(token);
            return jwt.getExpiresAt();
        } catch (Exception e) {
            log.error("获取Token过期时间失败", e);
            throw new RuntimeException("获取Token过期时间失败", e);
        }
    }
    
    /**
     * 刷新Token
     */
    public String refreshToken(String refreshToken) {
        try {
            if (!validateToken(refreshToken)) {
                throw new RuntimeException("刷新Token无效");
            }
            
            Long userId = getUserIdFromToken(refreshToken);
            String username = getUsernameFromToken(refreshToken);
            String userType = getUserTypeFromToken(refreshToken);
            
            if ("user".equals(userType)) {
                return generateUserToken(userId, username);
            } else if ("admin".equals(userType)) {
                return generateAdminToken(userId, username);
            } else {
                throw new RuntimeException("无效的用户类型");
            }
        } catch (Exception e) {
            log.error("刷新Token失败", e);
            throw new RuntimeException("刷新Token失败", e);
        }
    }
    
    /**
     * 解码JWT
     */
    private DecodedJWT getDecodedJWT(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }
    
    /**
     * 从Token中提取Token字符串（去除Bearer前缀）
     */
    public String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}