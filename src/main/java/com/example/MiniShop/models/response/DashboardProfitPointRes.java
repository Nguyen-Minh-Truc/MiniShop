package com.example.MiniShop.models.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardProfitPointRes {
  private String period;
  private BigDecimal revenue;
  private BigDecimal purchaseCost;
  private BigDecimal profit;
}
