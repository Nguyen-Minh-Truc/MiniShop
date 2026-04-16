package com.example.MiniShop.repository;

import com.example.MiniShop.models.entity.Inventory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository
    extends JpaRepository<Inventory, Long>,
            JpaSpecificationExecutor<Inventory> {
  boolean existsByProductId(Long productId);

  Optional<Inventory> findByProductId(Long productId);
}