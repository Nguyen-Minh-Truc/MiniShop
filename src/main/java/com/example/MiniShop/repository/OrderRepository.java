package com.example.MiniShop.repository;

import com.example.MiniShop.models.entity.Order;
import com.example.MiniShop.util.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findAllByStatusAndExpiredAtLessThanEqual(OrderStatus status,
                                                       LocalDateTime now);

  Page<Order> findAllByUserId(Long userId, Pageable pageable);

  long countByStatus(OrderStatus status);

  long countByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime from,
               LocalDateTime to);

  @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status = :status")
  BigDecimal sumTotalPriceByStatus(@Param("status") OrderStatus status);

  @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o " +
    "WHERE o.status = :status AND o.createdAt BETWEEN :from AND :to")
  BigDecimal
  sumTotalPriceByStatusAndCreatedAtBetween(@Param("status") OrderStatus status,
             @Param("from") LocalDateTime from,
             @Param("to") LocalDateTime to);

  @Query("SELECT o.status, COUNT(o.id) FROM Order o GROUP BY o.status")
  List<Object[]> countOrdersByStatus();

  @Query("SELECT YEAR(o.createdAt), MONTH(o.createdAt), COALESCE(SUM(o.totalPrice), 0), COUNT(o.id) " +
    "FROM Order o " +
    "WHERE o.status = :status AND o.createdAt BETWEEN :from AND :to " +
    "GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) " +
    "ORDER BY YEAR(o.createdAt), MONTH(o.createdAt)")
  List<Object[]> revenueByMonth(@Param("status") OrderStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}