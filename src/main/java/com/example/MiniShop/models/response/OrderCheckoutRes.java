package com.example.MiniShop.models.response;

import com.example.MiniShop.util.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCheckoutRes {
  private Long id;
  private Long userId;
  private OrderStatus status;
  private BigDecimal totalPrice;
  private LocalDateTime expiredAt;
  private List<OrderItemCheckoutRes> items;
}