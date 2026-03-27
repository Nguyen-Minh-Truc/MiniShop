package com.example.MiniShop.repository;

import com.example.MiniShop.models.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderItemRepository
    extends JpaRepository<PurchaseOrderItem, Long> {}
