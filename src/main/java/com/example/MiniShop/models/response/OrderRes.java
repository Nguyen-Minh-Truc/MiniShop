package com.example.MiniShop.models.response;

import com.example.MiniShop.util.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRes {
  private Long id;
  private Long userId;
  private String username;
  private OrderStatus status;
  private String shippingAddress;
  private String shippingPhone;
  private String methodPayment;
  private BigDecimal totalPrice;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime expiredAt;
  private List<OrderItemRes> items;
}