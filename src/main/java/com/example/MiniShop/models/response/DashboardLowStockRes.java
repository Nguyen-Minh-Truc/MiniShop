package com.example.MiniShop.models.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardLowStockRes {
  private long productId;
  private String productName;
  private int stock;
  private int reservedStock;
  private int availableStock;
}
