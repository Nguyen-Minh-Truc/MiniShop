package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.request.OrderPaymentReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.OrderCheckoutRes;
import com.example.MiniShop.models.response.OrderRes;
import com.example.MiniShop.services.OrderService;
import com.example.MiniShop.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  @PutMapping("/{id}/pay")
  @ApiMessage("Thanh toán đơn hàng thành công, đơn đang giao.")
  public ResponseEntity<OrderRes>
  payOrder(@PathVariable("id") Long id, @Valid @RequestBody OrderPaymentReq req)
      throws NotFoundException, ConflictException {
    return ResponseEntity.ok(orderService.payOrder(id, req));
  }

  @GetMapping("/me")
  @ApiMessage("Lấy danh sách đơn hàng của tôi thành công.")
  public ResponseEntity<ApiResponsePagination> getMyOrders(Pageable pageable)
      throws NotFoundException {
    return ResponseEntity.ok(orderService.getMyOrders(pageable));
  }

  @GetMapping
  @ApiMessage("Lấy toàn bộ đơn hàng thành công.")
  public ResponseEntity<ApiResponsePagination> getAllOrders(Pageable pageable) {
    return ResponseEntity.ok(orderService.getAllOrders(pageable));
  }

  @GetMapping("/{id}")
  @ApiMessage("Lấy chi tiết đơn hàng thành công.")
  public ResponseEntity<OrderRes> getOrderDetail(@PathVariable("id") Long id)
      throws NotFoundException {
    return ResponseEntity.ok(orderService.getOrderDetail(id));
  }

  @PutMapping("/{id}/cancel")
  @ApiMessage("Hủy đơn hàng thành công.")
  public ResponseEntity<OrderRes> cancelOrder(@PathVariable("id") Long id)
      throws NotFoundException, ConflictException {
    return ResponseEntity.ok(orderService.cancelOrder(id));
  }

  @PutMapping("/{id}/success")
  @ApiMessage("Cập nhật đơn hàng giao thành công.")
  public ResponseEntity<OrderRes> markOrderSuccess(@PathVariable("id") Long id)
      throws NotFoundException, ConflictException {
    return ResponseEntity.ok(orderService.markOrderSuccess(id));
  }
}