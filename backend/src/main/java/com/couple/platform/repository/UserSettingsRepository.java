package com.couple.platform.repository;

import com.couple.platform.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户设置数据访问层
 */
@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    
    /**
     * 根据用户ID查找用户设置
     */
    Optional<UserSettings> findByUserId(Long userId);
    
    /**
     * 检查用户设置是否存在
     */
    boolean existsByUserId(Long userId);
    
    /**
     * 删除用户设置
     */
    void deleteByUserId(Long userId);
}