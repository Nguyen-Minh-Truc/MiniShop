package com.example.MiniShop.models.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemCheckoutRes {
  private Long id;
  private Long productId;
  private String productName;
  private BigDecimal unitPrice;
  private int quantity;
  private BigDecimal itemTotal;
  private String promotionName;
}