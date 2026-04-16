package com.example.MiniShop.models.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryDetailDto {
  private long id;
  private int stock;
  private int reservedStock;
  private long productId;
  private String productName;
}