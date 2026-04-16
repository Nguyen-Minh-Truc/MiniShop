package com.example.MiniShop.repository;

import com.example.MiniShop.models.entity.OrderDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
  List<OrderDetail> findAllByOrderId(Long orderId);
}