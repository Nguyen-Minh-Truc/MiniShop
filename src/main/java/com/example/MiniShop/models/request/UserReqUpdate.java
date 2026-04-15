package com.example.MiniShop.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReqUpdate {
  @NotBlank(message = "Tên người dùng không được để trống.")
  private String username;

  @NotBlank(message = "Email không được để trống.")
  private String email;

  private String password;

  private String address;

  private String phone;

  private Boolean active;

  private Long roleId;
}