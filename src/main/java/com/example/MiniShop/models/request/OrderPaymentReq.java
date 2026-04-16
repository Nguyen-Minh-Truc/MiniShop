package com.example.MiniShop.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPaymentReq {
  @NotBlank(message = "Địa chỉ giao hàng không được để trống.")
  private String shippingAddress;

  @NotBlank(message = "Số điện thoại giao hàng không được để trống.")
  private String shippingPhone;

  @NotBlank(message = "Phương thức thanh toán không được để trống.")
  private String paymentMethod;
}