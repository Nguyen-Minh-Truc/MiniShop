package com.example.MiniShop.repository;

import com.example.MiniShop.util.enums.PurchaseOrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.MiniShop.models.entity.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>,
                                       JpaSpecificationExecutor<PurchaseOrder> {

  @Query("SELECT YEAR(po.createdAt), MONTH(po.createdAt), COALESCE(SUM(po.totalPrice), 0), COUNT(po.id) " +
      "FROM PurchaseOrder po " +
      "WHERE po.status = :status AND po.createdAt BETWEEN :from AND :to " +
      "GROUP BY YEAR(po.createdAt), MONTH(po.createdAt) " +
      "ORDER BY YEAR(po.createdAt), MONTH(po.createdAt)")
  List<Object[]> purchaseCostByMonth(@Param("status") PurchaseOrderStatus status,
                      @Param("from") LocalDateTime from,
                      @Param("to") LocalDateTime to);

}
