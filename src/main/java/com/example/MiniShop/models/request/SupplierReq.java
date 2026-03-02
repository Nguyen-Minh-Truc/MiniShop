package com.example.MiniShop.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierReq {

  @NotBlank(message = "Tên nhà cung cấp không được để trống.")
  private String name;

  @NotBlank(message = "số điện thoại nhà cung cấp không được để trống.")
  private String phone;

  @NotBlank(message = "Email nhà cung cấp không được để trống.")
  private String email;

  @NotBlank(message = "Địa chỉ nhà cung cấp không được để trống.")
  private String address;
}
