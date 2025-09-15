package com.couple.platform.repository;

import com.couple.platform.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问层
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 根据订单号查找订单
     */
    Optional<Order> findByOrderNo(String orderNo);
    
    /**
     * 根据创建者ID查找订单
     */
    List<Order> findByCreatorIdOrderByCreatedTimeDesc(Long creatorId);
    
    /**
     * 根据接收者ID查找订单
     */
    List<Order> findByReceiverIdOrderByCreatedTimeDesc(Long receiverId);
    
    /**
     * 根据创建者ID和状态查找订单
     */
    List<Order> findByCreatorIdAndStatusOrderByCreatedTimeDesc(Long creatorId, String status);
    
    /**
     * 根据接收者ID和状态查找订单
     */
    List<Order> findByReceiverIdAndStatusOrderByCreatedTimeDesc(Long receiverId, String status);
    
    /**
     * 查找用户相关的所有订单（创建的或接收的）
     */
    @Query("SELECT o FROM Order o WHERE o.creatorId = :userId OR o.receiverId = :userId ORDER BY o.createdTime DESC")
    List<Order> findUserRelatedOrders(@Param("userId") Long userId);
    
    /**
     * 查找用户相关的特定状态订单
     */
    @Query("SELECT o FROM Order o WHERE (o.creatorId = :userId OR o.receiverId = :userId) AND o.status = :status ORDER BY o.createdTime DESC")
    List<Order> findUserRelatedOrdersByStatus(@Param("userId") Long userId, @Param("status") String status);
    
    /**
     * 分页查找用户相关的订单
     */
    @Query("SELECT o FROM Order o WHERE o.creatorId = :userId OR o.receiverId = :userId ORDER BY o.createdTime DESC")
    Page<Order> findUserRelatedOrders(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 根据订单类型查找订单
     */
    List<Order> findByTypeOrderByCreatedTimeDesc(String type);
    
    /**
     * 根据状态查找订单
     */
    List<Order> findByStatusOrderByCreatedTimeDesc(String status);
    
    /**
     * 查找指定时间范围内的订单
     */
    @Query("SELECT o FROM Order o WHERE o.createdTime BETWEEN :startTime AND :endTime ORDER BY o.createdTime DESC")
    List<Order> findOrdersByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计用户创建的订单数量
     */
    long countByCreatorId(Long creatorId);
    
    /**
     * 统计用户接收的订单数量
     */
    long countByReceiverId(Long receiverId);
    
    /**
     * 统计特定状态的订单数量
     */
    long countByStatus(String status);
    
    /**
     * 统计用户在指定时间范围内创建的订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.creatorId = :userId AND o.createdTime BETWEEN :startTime AND :endTime")
    long countUserOrdersInTimeRange(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找待自动取消的订单（超过指定时间仍为pending状态）
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'pending' AND o.createdTime < :expireTime")
    List<Order> findExpiredPendingOrders(@Param("expireTime") LocalDateTime expireTime);
    
    /**
     * 查找热门订单类型统计
     */
    @Query("SELECT o.type, COUNT(o) as count FROM Order o GROUP BY o.type ORDER BY count DESC")
    List<Object[]> findPopularOrderTypes();
}