package com.couple.platform.repository;

import com.couple.platform.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单项数据访问层
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * 根据订单ID查找订单项
     */
    List<OrderItem> findByOrderIdOrderByCreatedTime(Long orderId);
    
    /**
     * 删除订单的所有订单项
     */
    void deleteByOrderId(Long orderId);
    
    /**
     * 统计订单的商品数量
     */
    long countByOrderId(Long orderId);
    
    /**
     * 计算订单的总金额
     */
    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.orderId = :orderId")
    BigDecimal sumTotalPriceByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 查找热门商品统计
     */
    @Query("SELECT oi.name, SUM(oi.quantity) as totalQuantity FROM OrderItem oi GROUP BY oi.name ORDER BY totalQuantity DESC")
    List<Object[]> findPopularItems();
}