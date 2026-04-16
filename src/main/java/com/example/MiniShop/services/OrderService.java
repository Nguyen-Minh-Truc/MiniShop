package com.example.MiniShop.services;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.request.OrderPaymentReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.OrderCheckoutRes;
import com.example.MiniShop.models.response.OrderRes;
import org.springframework.data.domain.Pageable;

public interface OrderService {
  OrderCheckoutRes checkoutFromMyCart()
      throws NotFoundException, ConflictException;

  OrderRes payOrder(Long id, OrderPaymentReq req)
      throws NotFoundException, ConflictException;

  ApiResponsePagination getMyOrders(Pageable pageable) throws NotFoundException;

  ApiResponsePagination getAllOrders(Pageable pageable);

  OrderRes getOrderDetail(Long id) throws NotFoundException;

  OrderRes cancelOrder(Long id) throws NotFoundException, ConflictException;

  OrderRes markOrderSuccess(Long id)
      throws NotFoundException, ConflictException;

  void autoCancelExpiredPendingOrders();
}