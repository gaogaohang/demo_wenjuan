package com.couple.platform.repository;

import com.couple.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(String phone);
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据配对码查找用户
     */
    Optional<User> findByPairCode(String pairCode);
    
    /**
     * 根据微信OpenID查找用户
     */
    Optional<User> findByWechatOpenid(String wechatOpenid);
    
    /**
     * 根据微信UnionID查找用户
     */
    Optional<User> findByWechatUnionid(String wechatUnionid);
    
    /**
     * 检查手机号是否已存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 检查用户名是否已存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查配对码是否已存在
     */
    boolean existsByPairCode(String pairCode);
    
    /**
     * 根据配对对象ID查找用户
     */
    Optional<User> findByPartnerId(Long partnerId);
    
    /**
     * 查找已配对的用户
     */
    @Query("SELECT u FROM User u WHERE u.isPaired = true AND u.status = 1")
    java.util.List<User> findPairedUsers();
    
    /**
     * 查找未配对的用户
     */
    @Query("SELECT u FROM User u WHERE u.isPaired = false AND u.status = 1")
    java.util.List<User> findUnpairedUsers();
    
    /**
     * 根据状态查找用户
     */
    java.util.List<User> findByStatus(Integer status);
    
    /**
     * 统计用户总数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 1")
    long countActiveUsers();
    
    /**
     * 统计已配对用户数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isPaired = true AND u.status = 1")
    long countPairedUsers();
    
    /**
     * 统计未配对用户数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isPaired = false AND u.status = 1")
    long countUnpairedUsers();
}