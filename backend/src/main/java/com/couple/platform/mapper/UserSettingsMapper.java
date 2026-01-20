package com.couple.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.couple.platform.entity.UserSettings;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

/**
 * 用户设置数据访问层 - MyBatis Plus
 */
@Mapper
public interface UserSettingsMapper extends BaseMapper<UserSettings> {
    
    /**
     * 根据用户ID查找用户设置
     */
    default Optional<UserSettings> findByUserId(Long userId) {
        UserSettings settings = selectOne(new QueryWrapper<UserSettings>().eq("user_id", userId));
        return Optional.ofNullable(settings);
    }
    
    /**
     * 检查用户设置是否存在
     */
    default boolean existsByUserId(Long userId) {
        return selectCount(new QueryWrapper<UserSettings>().eq("user_id", userId)) > 0;
    }
    
    /**
     * 删除用户设置
     */
    default int deleteByUserId(Long userId) {
        return delete(new QueryWrapper<UserSettings>().eq("user_id", userId));
    }
}