package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.response.OrderCheckoutRes;
import com.example.MiniShop.services.OrderService;
import com.example.MiniShop.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;

  @PostMapping("/checkout")
  @ApiMessage("Checkout thành công, đơn hàng chờ thanh toán trong 10 phút.")
  public ResponseEntity<OrderCheckoutRes> checkout()
      throws NotFoundException, ConflictException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(orderService.checkoutFromMyCart());
  }
}