package com.couple.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.couple.platform.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单项数据访问层 - MyBatis Plus
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    
    /**
     * 根据订单ID查找订单项
     */
    @Select("SELECT * FROM order_items WHERE order_id = #{orderId} ORDER BY created_time")
    List<OrderItem> findByOrderIdOrderByCreatedTime(@Param("orderId") Long orderId);
    
    /**
     * 删除订单的所有订单项
     */
    int deleteByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 统计订单的商品数量
     */
    long countByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 计算订单的总金额
     */
    @Select("SELECT SUM(total_price) FROM order_items WHERE order_id = #{orderId}")
    BigDecimal sumTotalPriceByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 查找热门商品统计
     */
    @Select("SELECT name, SUM(quantity) as total_quantity FROM order_items GROUP BY name ORDER BY total_quantity DESC LIMIT 10")
    List<Object[]> findPopularItems();
}