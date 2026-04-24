package com.example.MiniShop.repository;

import com.example.MiniShop.models.entity.Inventory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository
    extends JpaRepository<Inventory, Long>,
            JpaSpecificationExecutor<Inventory> {
  boolean existsByProductId(Long productId);

  Optional<Inventory> findByProductId(Long productId);

  @Query("SELECT i.product.id, i.product.name, i.stock, i.reserved_stock, " +
         "(i.stock - i.reserved_stock) " +
         "FROM Inventory i " +
         "WHERE (i.stock - i.reserved_stock) <= :threshold " +
         "ORDER BY (i.stock - i.reserved_stock) ASC")
  List<Object[]> findLowStock(@Param("threshold") int threshold);

  @Query("SELECT COUNT(i.id) FROM Inventory i WHERE (i.stock - i.reserved_stock) <= :threshold")
  long countLowStock(@Param("threshold") int threshold);
}