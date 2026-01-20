package com.couple.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.couple.platform.entity.OrderEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 订单评价数据访问层 - MyBatis Plus
 */
@Mapper
public interface OrderEvaluationMapper extends BaseMapper<OrderEvaluation> {
    
    /**
     * 根据订单ID查找评价
     */
    @Select("SELECT * FROM order_evaluations WHERE order_id = #{orderId} ORDER BY created_time DESC")
    List<OrderEvaluation> findByOrderIdOrderByCreatedTimeDesc(@Param("orderId") Long orderId);
    
    /**
     * 根据订单ID和评价者ID查找评价
     */
    @Select("SELECT * FROM order_evaluations WHERE order_id = #{orderId} AND evaluator_id = #{evaluatorId}")
    Optional<OrderEvaluation> findByOrderIdAndEvaluatorId(@Param("orderId") Long orderId, @Param("evaluatorId") Long evaluatorId);
    
    /**
     * 根据评价者ID查找评价
     */
    @Select("SELECT * FROM order_evaluations WHERE evaluator_id = #{evaluatorId} ORDER BY created_time DESC")
    List<OrderEvaluation> findByEvaluatorIdOrderByCreatedTimeDesc(@Param("evaluatorId") Long evaluatorId);
    
    /**
     * 检查用户是否已评价订单
     */
    boolean existsByOrderIdAndEvaluatorId(@Param("orderId") Long orderId, @Param("evaluatorId") Long evaluatorId);
    
    /**
     * 统计订单的评价数量
     */
    long countByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 统计用户的评价数量
     */
    long countByEvaluatorId(@Param("evaluatorId") Long evaluatorId);
    
    /**
     * 计算订单的平均评分
     */
    @Select("SELECT AVG(rating) FROM order_evaluations WHERE order_id = #{orderId}")
    Double calculateAverageRatingByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 统计各评分级别的数量
     */
    @Select("SELECT rating, COUNT(*) as count FROM order_evaluations GROUP BY rating ORDER BY rating")
    List<Object[]> countByRatingLevel();
}