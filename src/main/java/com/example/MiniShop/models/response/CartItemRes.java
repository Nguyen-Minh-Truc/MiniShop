package com.example.MiniShop.models.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRes {
  private Long id;
  private Long productId;
  private String productName;
  private BigDecimal price;
  private int quantity;
  private BigDecimal totalPrice;
}