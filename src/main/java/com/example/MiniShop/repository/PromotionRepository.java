package com.example.MiniShop.repository;

import com.example.MiniShop.models.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PromotionRepository
    extends JpaRepository<Promotion, Long>,
            JpaSpecificationExecutor<Promotion> {}
