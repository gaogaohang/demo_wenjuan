package com.couple.platform.repository;

import com.couple.platform.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 管理员数据访问层
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
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
    java.util.List<Admin> findByStatus(Integer status);
    
    /**
     * 根据角色查找管理员
     */
    java.util.List<Admin> findByRole(String role);
    
    /**
     * 查找活跃的管理员
     */
    @Query("SELECT a FROM Admin a WHERE a.status = 1")
    java.util.List<Admin> findActiveAdmins();
    
    /**
     * 统计管理员总数
     */
    @Query("SELECT COUNT(a) FROM Admin a WHERE a.status = 1")
    long countActiveAdmins();
    
    /**
     * 统计超级管理员数量
     */
    @Query("SELECT COUNT(a) FROM Admin a WHERE a.role = 'super_admin' AND a.status = 1")
    long countSuperAdmins();
}