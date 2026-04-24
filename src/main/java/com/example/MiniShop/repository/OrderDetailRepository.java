package com.example.MiniShop.repository;

import com.example.MiniShop.models.entity.OrderDetail;
import com.example.MiniShop.util.enums.OrderStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
  List<OrderDetail> findAllByOrderId(Long orderId);

  @Query("SELECT od.product.id, od.product.name, COALESCE(SUM(od.quantity), 0), COALESCE(SUM(od.totalPrice), 0) " +
         "FROM OrderDetail od " +
         "WHERE od.order.status = :status " +
         "GROUP BY od.product.id, od.product.name " +
         "ORDER BY SUM(od.quantity) DESC")
  List<Object[]> findTopProductsByStatus(@Param("status") OrderStatus status,
                                         Pageable pageable);
}