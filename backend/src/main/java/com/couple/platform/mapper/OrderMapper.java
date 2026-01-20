package com.couple.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.couple.platform.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问层 - MyBatis Plus
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 根据订单号查找订单
     */
    Optional<Order> findByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 根据创建者ID查找订单
     */
    @Select("SELECT * FROM orders WHERE creator_id = #{creatorId} ORDER BY created_time DESC")
    List<Order> findByCreatorIdOrderByCreatedTimeDesc(@Param("creatorId") Long creatorId);
    
    /**
     * 根据接收者ID查找订单
     */
    @Select("SELECT * FROM orders WHERE receiver_id = #{receiverId} ORDER BY created_time DESC")
    List<Order> findByReceiverIdOrderByCreatedTimeDesc(@Param("receiverId") Long receiverId);
    
    /**
     * 根据创建者ID和状态查找订单
     */
    @Select("SELECT * FROM orders WHERE creator_id = #{creatorId} AND status = #{status} ORDER BY created_time DESC")
    List<Order> findByCreatorIdAndStatusOrderByCreatedTimeDesc(@Param("creatorId") Long creatorId, @Param("status") String status);
    
    /**
     * 根据接收者ID和状态查找订单
     */
    @Select("SELECT * FROM orders WHERE receiver_id = #{receiverId} AND status = #{status} ORDER BY created_time DESC")
    List<Order> findByReceiverIdAndStatusOrderByCreatedTimeDesc(@Param("receiverId") Long receiverId, @Param("status") String status);
    
    /**
     * 查找用户相关的所有订单（创建的或接收的）
     */
    @Select("SELECT * FROM orders WHERE creator_id = #{userId} OR receiver_id = #{userId} ORDER BY created_time DESC")
    List<Order> findUserRelatedOrders(@Param("userId") Long userId);
    
    /**
     * 查找用户相关的所有订单（分页）
     */
    default IPage<Order> findUserRelatedOrdersPage(Page<Order> page, @Param("userId") Long userId) {
        return this.selectPage(page, null);
    }
}