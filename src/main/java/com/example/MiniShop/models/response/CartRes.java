package com.example.MiniShop.models.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRes {
  private Long id;
  private Long userId;
  private String username;
  private BigDecimal totalPrice;
  private List<CartItemRes> items;
}