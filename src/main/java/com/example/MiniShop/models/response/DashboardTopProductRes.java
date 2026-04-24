package com.example.MiniShop.models.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardTopProductRes {
  private long productId;
  private String productName;
  private long totalQuantity;
  private BigDecimal totalRevenue;
}
