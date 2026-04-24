package com.example.MiniShop.services;

import com.example.MiniShop.models.response.DashboardLowStockRes;
import com.example.MiniShop.models.response.DashboardNewUsersPointRes;
import com.example.MiniShop.models.response.DashboardOrderStatusRes;
import com.example.MiniShop.models.response.DashboardProfitPointRes;
import com.example.MiniShop.models.response.DashboardPurchaseCostPointRes;
import com.example.MiniShop.models.response.DashboardRevenuePointRes;
import com.example.MiniShop.models.response.DashboardStatsRes;
import com.example.MiniShop.models.response.DashboardTopProductRes;
import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
  DashboardStatsRes getStats(Integer lowStockThreshold);

  List<DashboardOrderStatusRes> getOrdersByStatus();

  List<DashboardRevenuePointRes> getRevenue(LocalDate fromDate,
                                            LocalDate toDate);

  List<DashboardTopProductRes> getTopProducts(Integer limit);

  List<DashboardLowStockRes> getLowStock(Integer threshold);

  List<DashboardProfitPointRes> getProfit(LocalDate fromDate, LocalDate toDate);

  List<DashboardPurchaseCostPointRes> getPurchaseCost(LocalDate fromDate,
                                                      LocalDate toDate);

  List<DashboardNewUsersPointRes> getNewUsers(LocalDate fromDate,
                                              LocalDate toDate);
}
