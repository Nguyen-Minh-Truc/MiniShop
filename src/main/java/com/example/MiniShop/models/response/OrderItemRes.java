package com.example.MiniShop.models.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRes {
  private Long id;
  private Long productId;
  private String productName;
  private BigDecimal unitPrice;
  private int quantity;
  private BigDecimal itemTotal;
}