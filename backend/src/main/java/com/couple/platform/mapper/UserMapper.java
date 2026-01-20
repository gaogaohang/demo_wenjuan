package com.couple.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.couple.platform.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层 - MyBatis Plus
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(@Param("phone") String phone);
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(@Param("username") String username);
    
    /**
     * 根据配对码查找用户
     */
    Optional<User> findByPairCode(@Param("pairCode") String pairCode);
    
    /**
     * 根据微信OpenID查找用户
     */
    Optional<User> findByWechatOpenid(@Param("wechatOpenid") String wechatOpenid);
    
    /**
     * 根据微信UnionID查找用户
     */
    Optional<User> findByWechatUnionid(@Param("wechatUnionid") String wechatUnionid);
    
    /**
     * 检查手机号是否已存在
     */
    boolean existsByPhone(@Param("phone") String phone);
    
    /**
     * 检查用户名是否已存在
     */
    boolean existsByUsername(@Param("username") String username);
    
    /**
     * 检查配对码是否已存在
     */
    boolean existsByPairCode(@Param("pairCode") String pairCode);
    
    /**
     * 根据配对对象ID查找用户
     */
    Optional<User> findByPartnerId(@Param("partnerId") Long partnerId);
    
    /**
     * 查找已配对的用户
     */
    @Select("SELECT * FROM users WHERE is_paired = true AND status = 1")
    List<User> findPairedUsers();
    
    /**
     * 查找未配对的用户
     */
    @Select("SELECT * FROM users WHERE is_paired = false AND status = 1")
    List<User> findUnpairedUsers();
    
    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(@Param("status") Integer status);
    
    /**
     * 统计用户总数
     */
    @Select("SELECT COUNT(*) FROM users WHERE status = 1")
    long countActiveUsers();
    
    /**
     * 统计已配对用户数
     */
    @Select("SELECT COUNT(*) FROM users WHERE is_paired = true AND status = 1")
    long countPairedUsers();
    
    /**
     * 统计未配对用户数
     */
    @Select("SELECT COUNT(*) FROM users WHERE is_paired = false AND status = 1")
    long countUnpairedUsers();
}