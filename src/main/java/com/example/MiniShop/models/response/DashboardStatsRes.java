package com.example.MiniShop.models.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardStatsRes {
  private long totalOrders;
  private BigDecimal totalRevenue;
  private long totalUsers;
  private long totalProducts;
  private long lowStockProducts;
  private long ordersThisMonth;
  private BigDecimal revenueThisMonth;
}
