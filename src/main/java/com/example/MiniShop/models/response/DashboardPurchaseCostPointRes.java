package com.example.MiniShop.models.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardPurchaseCostPointRes {
  private String period;
  private long purchaseOrderCount;
  private BigDecimal purchaseCost;
}
