package com.couple.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.couple.platform.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 管理员数据访问层 - MyBatis Plus
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    
    /**
     * 根据用户名查找管理员
     */
    Optional<Admin> findByUsername(String username);
    
    /**
     * 根据邮箱查找管理员
     */
    Optional<Admin> findByEmail(String email);
    
    /**
     * 检查用户名是否已存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否已存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据状态查找管理员
     */
    List<Admin> findByStatus(Integer status);
    
    /**
     * 根据角色查找管理员
     */
    List<Admin> findByRole(String role);
    
    /**
     * 查找活跃的管理员
     */
    @Select("SELECT * FROM admins WHERE status = 1")
    List<Admin> findActiveAdmins();
    
    /**
     * 统计管理员总数
     */
    @Select("SELECT COUNT(*) FROM admins WHERE status = 1")
    long countActiveAdmins();
    
    /**
     * 统计超级管理员数量
     */
    @Select("SELECT COUNT(*) FROM admins WHERE role = 'super_admin'")
    long countSuperAdmins();
}