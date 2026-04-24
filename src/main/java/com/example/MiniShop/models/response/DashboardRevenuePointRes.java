package com.example.MiniShop.models.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardRevenuePointRes {
  private String period;
  private long orderCount;
  private BigDecimal revenue;
}
