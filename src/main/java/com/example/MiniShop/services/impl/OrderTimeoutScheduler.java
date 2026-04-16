package com.example.MiniShop.services.impl;

import com.example.MiniShop.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderTimeoutScheduler {
  private final OrderService orderService;

  @Scheduled(fixedDelay = 60000)
  public void autoCancelPendingOrders() {
    orderService.autoCancelExpiredPendingOrders();
  }
}