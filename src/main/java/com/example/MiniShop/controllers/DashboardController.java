package com.example.MiniShop.controllers;

import com.example.MiniShop.models.response.DashboardLowStockRes;
import com.example.MiniShop.models.response.DashboardNewUsersPointRes;
import com.example.MiniShop.models.response.DashboardOrderStatusRes;
import com.example.MiniShop.models.response.DashboardProfitPointRes;
import com.example.MiniShop.models.response.DashboardPurchaseCostPointRes;
import com.example.MiniShop.models.response.DashboardRevenuePointRes;
import com.example.MiniShop.models.response.DashboardStatsRes;
import com.example.MiniShop.models.response.DashboardTopProductRes;
import com.example.MiniShop.services.DashboardService;
import com.example.MiniShop.util.annotation.ApiMessage;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
  private final DashboardService dashboardService;

  @GetMapping("/stats")
  @ApiMessage("Lấy thống kê tổng quan dashboard thành công.")
  public ResponseEntity<DashboardStatsRes>
  getStats(@RequestParam(value = "lowStockThreshold", required = false)
           Integer lowStockThreshold) {
    return ResponseEntity.ok(dashboardService.getStats(lowStockThreshold));
  }

  @GetMapping("/orders/by-status")
  @ApiMessage("Lấy thống kê đơn hàng theo trạng thái thành công.")
  public ResponseEntity<List<DashboardOrderStatusRes>> getOrdersByStatus() {
    return ResponseEntity.ok(dashboardService.getOrdersByStatus());
  }

  @GetMapping("/revenue")
  @ApiMessage("Lấy thống kê doanh thu theo tháng thành công.")
  public ResponseEntity<List<DashboardRevenuePointRes>> getRevenue(
      @RequestParam(value = "fromDate", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(value = "toDate", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
    return ResponseEntity.ok(dashboardService.getRevenue(fromDate, toDate));
  }

  @GetMapping("/products/top")
  @ApiMessage("Lấy danh sách sản phẩm bán chạy thành công.")
  public ResponseEntity<List<DashboardTopProductRes>>
  getTopProducts(@RequestParam(value = "limit", required = false)
                 Integer limit) {
    return ResponseEntity.ok(dashboardService.getTopProducts(limit));
  }

  @GetMapping("/inventory/low-stock")
  @ApiMessage("Lấy danh sách sản phẩm tồn kho thấp thành công.")
  public ResponseEntity<List<DashboardLowStockRes>>
  getLowStock(@RequestParam(value = "threshold", required = false)
              Integer threshold) {
    return ResponseEntity.ok(dashboardService.getLowStock(threshold));
  }

  @GetMapping("/profit")
  @ApiMessage("Lấy thống kê lợi nhuận theo tháng thành công.")
  public ResponseEntity<List<DashboardProfitPointRes>> getProfit(
      @RequestParam(value = "fromDate", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(value = "toDate", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
    return ResponseEntity.ok(dashboardService.getProfit(fromDate, toDate));
  }

  @GetMapping("/purchase-cost")
  @ApiMessage("Lấy thống kê chi phí nhập hàng theo tháng thành công.")
  public ResponseEntity<List<DashboardPurchaseCostPointRes>> getPurchaseCost(
      @RequestParam(value = "fromDate", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(value = "toDate", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
    return ResponseEntity.ok(dashboardService.getPurchaseCost(fromDate, toDate));
  }

  @GetMapping("/users/new")
  @ApiMessage("Lấy thống kê người dùng mới theo tháng thành công.")
  public ResponseEntity<List<DashboardNewUsersPointRes>> getNewUsers(
      @RequestParam(value = "fromDate", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(value = "toDate", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
    return ResponseEntity.ok(dashboardService.getNewUsers(fromDate, toDate));
  }
}
