package com.example.MiniShop.repository;

import com.example.MiniShop.models.entity.Order;
import com.example.MiniShop.util.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findAllByStatusAndExpiredAtLessThanEqual(OrderStatus status,
                                                       LocalDateTime now);

  Page<Order> findAllByUserId(Long userId, Pageable pageable);
}