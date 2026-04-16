package com.example.MiniShop.repository;

import com.example.MiniShop.models.entity.Promotion;
import com.example.MiniShop.util.enums.PromotionStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PromotionRepository
    extends JpaRepository<Promotion, Long>,
            JpaSpecificationExecutor<Promotion> {
  List<Promotion> findAllByProductIdAndStatusAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
      Long productId, PromotionStatus status, LocalDateTime nowStart,
      LocalDateTime nowEnd);
}
