package com.example.MiniShop.services.impl;

import com.example.MiniShop.models.response.DashboardLowStockRes;
import com.example.MiniShop.models.response.DashboardNewUsersPointRes;
import com.example.MiniShop.models.response.DashboardOrderStatusRes;
import com.example.MiniShop.models.response.DashboardProfitPointRes;
import com.example.MiniShop.models.response.DashboardPurchaseCostPointRes;
import com.example.MiniShop.models.response.DashboardRevenuePointRes;
import com.example.MiniShop.models.response.DashboardStatsRes;
import com.example.MiniShop.models.response.DashboardTopProductRes;
import com.example.MiniShop.repository.InventoryRepository;
import com.example.MiniShop.repository.OrderDetailRepository;
import com.example.MiniShop.repository.OrderRepository;
import com.example.MiniShop.repository.ProductRepository;
import com.example.MiniShop.repository.PurchaseOrderRepository;
import com.example.MiniShop.repository.UserRepository;
import com.example.MiniShop.services.DashboardService;
import com.example.MiniShop.util.enums.OrderStatus;
import com.example.MiniShop.util.enums.PurchaseOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
  private static final int DEFAULT_THRESHOLD = 10;
  private static final int DEFAULT_TOP_LIMIT = 10;

  private final OrderRepository orderRepository;
  private final OrderDetailRepository orderDetailRepository;
  private final ProductRepository productRepository;
  private final InventoryRepository inventoryRepository;
  private final PurchaseOrderRepository purchaseOrderRepository;
  private final UserRepository userRepository;

  @Override
  public DashboardStatsRes getStats(Integer lowStockThreshold) {
    DashboardStatsRes res = new DashboardStatsRes();
    int threshold = lowStockThreshold == null ? DEFAULT_THRESHOLD : lowStockThreshold;

    BigDecimal totalRevenue = orderRepository.sumTotalPriceByStatus(OrderStatus.SUCCESS);
    long totalOrders = orderRepository.countByStatus(OrderStatus.SUCCESS);
    long totalUsers = userRepository.count();
    long totalProducts = productRepository.countByActiveTrue();
    long lowStockProducts = inventoryRepository.countLowStock(threshold);

    YearMonth currentMonth = YearMonth.now();
    LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
    LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);

    BigDecimal revenueThisMonth =
        orderRepository.sumTotalPriceByStatusAndCreatedAtBetween(OrderStatus.SUCCESS,
                                                                 monthStart,
                                                                 monthEnd);
    long ordersThisMonth =
        orderRepository.countByStatusAndCreatedAtBetween(OrderStatus.SUCCESS,
                                                         monthStart,
                                                         monthEnd);

    res.setTotalRevenue(nullToZero(totalRevenue));
    res.setTotalOrders(totalOrders);
    res.setTotalUsers(totalUsers);
    res.setTotalProducts(totalProducts);
    res.setLowStockProducts(lowStockProducts);
    res.setRevenueThisMonth(nullToZero(revenueThisMonth));
    res.setOrdersThisMonth(ordersThisMonth);
    return res;
  }

  @Override
  public List<DashboardOrderStatusRes> getOrdersByStatus() {
    List<Object[]> rows = orderRepository.countOrdersByStatus();
    List<DashboardOrderStatusRes> result = new ArrayList<>();

    for (Object[] row : rows) {
      DashboardOrderStatusRes item = new DashboardOrderStatusRes();
      item.setStatus(String.valueOf(row[0]));
      item.setCount(castLong(row[1]));
      result.add(item);
    }

    result.sort(Comparator.comparing(DashboardOrderStatusRes::getStatus));
    return result;
  }

  @Override
  public List<DashboardRevenuePointRes> getRevenue(LocalDate fromDate,
                                                   LocalDate toDate) {
    DateRange range = normalizeDateRange(fromDate, toDate);
    List<Object[]> rows = orderRepository.revenueByMonth(OrderStatus.SUCCESS,
                                                         range.from,
                                                         range.to);
    List<DashboardRevenuePointRes> result = new ArrayList<>();

    for (Object[] row : rows) {
      DashboardRevenuePointRes item = new DashboardRevenuePointRes();
      item.setPeriod(toPeriod(row[0], row[1]));
      item.setOrderCount(castLong(row[3]));
      item.setRevenue(nullToZero((BigDecimal)row[2]));
      result.add(item);
    }

    return result;
  }

  @Override
  public List<DashboardTopProductRes> getTopProducts(Integer limit) {
    int top = limit == null || limit < 1 ? DEFAULT_TOP_LIMIT : limit;
    List<Object[]> rows = orderDetailRepository.findTopProductsByStatus(
        OrderStatus.SUCCESS, PageRequest.of(0, top));
    List<DashboardTopProductRes> result = new ArrayList<>();

    for (Object[] row : rows) {
      DashboardTopProductRes item = new DashboardTopProductRes();
      item.setProductId(castLong(row[0]));
      item.setProductName((String)row[1]);
      item.setTotalQuantity(castLong(row[2]));
      item.setTotalRevenue(nullToZero((BigDecimal)row[3]));
      result.add(item);
    }

    return result;
  }

  @Override
  public List<DashboardLowStockRes> getLowStock(Integer threshold) {
    int lowStockThreshold = threshold == null ? DEFAULT_THRESHOLD : threshold;
    List<Object[]> rows = inventoryRepository.findLowStock(lowStockThreshold);
    List<DashboardLowStockRes> result = new ArrayList<>();

    for (Object[] row : rows) {
      DashboardLowStockRes item = new DashboardLowStockRes();
      item.setProductId(castLong(row[0]));
      item.setProductName((String)row[1]);
      item.setStock(castInt(row[2]));
      item.setReservedStock(castInt(row[3]));
      item.setAvailableStock(castInt(row[4]));
      result.add(item);
    }

    return result;
  }

  @Override
  public List<DashboardProfitPointRes> getProfit(LocalDate fromDate,
                                                 LocalDate toDate) {
    DateRange range = normalizeDateRange(fromDate, toDate);
    List<Object[]> revenueRows = orderRepository.revenueByMonth(OrderStatus.SUCCESS,
                                                                range.from,
                                                                range.to);
    List<Object[]> costRows = purchaseOrderRepository.purchaseCostByMonth(
        PurchaseOrderStatus.SUCCESS, range.from, range.to);

    Map<String, BigDecimal> revenueByPeriod = new LinkedHashMap<>();
    Map<String, BigDecimal> costByPeriod = new LinkedHashMap<>();

    for (Object[] row : revenueRows) {
      String period = toPeriod(row[0], row[1]);
      revenueByPeriod.put(period, nullToZero((BigDecimal)row[2]));
    }
    for (Object[] row : costRows) {
      String period = toPeriod(row[0], row[1]);
      costByPeriod.put(period, nullToZero((BigDecimal)row[2]));
    }

    List<String> periods = new ArrayList<>();
    periods.addAll(revenueByPeriod.keySet());
    for (String period : costByPeriod.keySet()) {
      if (!periods.contains(period)) {
        periods.add(period);
      }
    }
    periods.sort(String::compareTo);

    List<DashboardProfitPointRes> result = new ArrayList<>();
    for (String period : periods) {
      BigDecimal revenue = revenueByPeriod.getOrDefault(period, BigDecimal.ZERO);
      BigDecimal cost = costByPeriod.getOrDefault(period, BigDecimal.ZERO);

      DashboardProfitPointRes item = new DashboardProfitPointRes();
      item.setPeriod(period);
      item.setRevenue(revenue);
      item.setPurchaseCost(cost);
      item.setProfit(revenue.subtract(cost));
      result.add(item);
    }

    return result;
  }

  @Override
  public List<DashboardPurchaseCostPointRes> getPurchaseCost(LocalDate fromDate,
                                                             LocalDate toDate) {
    DateRange range = normalizeDateRange(fromDate, toDate);
    List<Object[]> rows = purchaseOrderRepository.purchaseCostByMonth(
        PurchaseOrderStatus.SUCCESS, range.from, range.to);

    List<DashboardPurchaseCostPointRes> result = new ArrayList<>();
    for (Object[] row : rows) {
      DashboardPurchaseCostPointRes item = new DashboardPurchaseCostPointRes();
      item.setPeriod(toPeriod(row[0], row[1]));
      item.setPurchaseCost(nullToZero((BigDecimal)row[2]));
      item.setPurchaseOrderCount(castLong(row[3]));
      result.add(item);
    }

    return result;
  }

  @Override
  public List<DashboardNewUsersPointRes> getNewUsers(LocalDate fromDate,
                                                     LocalDate toDate) {
    DateRange range = normalizeDateRange(fromDate, toDate);
    List<Object[]> rows = userRepository.countNewUsersByMonth(range.from,
                                                              range.to);

    List<DashboardNewUsersPointRes> result = new ArrayList<>();
    for (Object[] row : rows) {
      DashboardNewUsersPointRes item = new DashboardNewUsersPointRes();
      item.setPeriod(toPeriod(row[0], row[1]));
      item.setNewUsers(castLong(row[2]));
      result.add(item);
    }

    return result;
  }

  private DateRange normalizeDateRange(LocalDate fromDate, LocalDate toDate) {
    LocalDate end = toDate == null ? LocalDate.now() : toDate;
    LocalDate start = fromDate == null ? end.minusMonths(11).withDayOfMonth(1)
                                       : fromDate;
    if (start.isAfter(end)) {
      LocalDate tmp = start;
      start = end;
      end = tmp;
    }

    return new DateRange(start.atStartOfDay(), end.atTime(23, 59, 59));
  }

  private BigDecimal nullToZero(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }

  private long castLong(Object value) {
    return value == null ? 0L : ((Number)value).longValue();
  }

  private int castInt(Object value) {
    return value == null ? 0 : ((Number)value).intValue();
  }

  private String toPeriod(Object yearObj, Object monthObj) {
    int year = castInt(yearObj);
    int month = castInt(monthObj);
    return String.format("%04d-%02d", year, month);
  }

  private record DateRange(LocalDateTime from, LocalDateTime to) {}
}
