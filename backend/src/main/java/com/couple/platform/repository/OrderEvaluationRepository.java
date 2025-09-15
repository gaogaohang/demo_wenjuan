package com.couple.platform.repository;

import com.couple.platform.entity.OrderEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 订单评价数据访问层
 */
@Repository
public interface OrderEvaluationRepository extends JpaRepository<OrderEvaluation, Long> {
    
    /**
     * 根据订单ID查找评价
     */
    List<OrderEvaluation> findByOrderIdOrderByCreatedTimeDesc(Long orderId);
    
    /**
     * 根据订单ID和评价者ID查找评价
     */
    Optional<OrderEvaluation> findByOrderIdAndEvaluatorId(Long orderId, Long evaluatorId);
    
    /**
     * 根据评价者ID查找评价
     */
    List<OrderEvaluation> findByEvaluatorIdOrderByCreatedTimeDesc(Long evaluatorId);
    
    /**
     * 检查用户是否已评价订单
     */
    boolean existsByOrderIdAndEvaluatorId(Long orderId, Long evaluatorId);
    
    /**
     * 统计订单的评价数量
     */
    long countByOrderId(Long orderId);
    
    /**
     * 统计用户的评价数量
     */
    long countByEvaluatorId(Long evaluatorId);
    
    /**
     * 计算订单的平均评分
     */
    @Query("SELECT AVG(oe.rating) FROM OrderEvaluation oe WHERE oe.orderId = :orderId")
    Double calculateAverageRatingByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 统计各评分级别的数量
     */
    @Query("SELECT oe.rating, COUNT(oe) FROM OrderEvaluation oe GROUP BY oe.rating ORDER BY oe.rating")
    List<Object[]> countByRatingLevel();
    
    /**
     * 查找最新的评价
     */
    List<OrderEvaluation> findTop10ByOrderByCreatedTimeDesc();
    
    /**
     * 查找好评（评分>=4）
     */
    @Query("SELECT oe FROM OrderEvaluation oe WHERE oe.rating >= 4 ORDER BY oe.createdTime DESC")
    List<OrderEvaluation> findPositiveEvaluations();
    
    /**
     * 查找差评（评分<=2）
     */
    @Query("SELECT oe FROM OrderEvaluation oe WHERE oe.rating <= 2 ORDER BY oe.createdTime DESC")
    List<OrderEvaluation> findNegativeEvaluations();
}