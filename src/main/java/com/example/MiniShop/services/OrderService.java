package com.example.MiniShop.services;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.response.OrderCheckoutRes;

public interface OrderService {
  OrderCheckoutRes checkoutFromMyCart() throws NotFoundException, ConflictException;

  void autoCancelExpiredPendingOrders();
}