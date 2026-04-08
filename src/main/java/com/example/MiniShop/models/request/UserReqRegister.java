package com.example.MiniShop.models.request;

import com.example.MiniShop.util.annotation.UniqueEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReqRegister {
  @NotBlank(message = "Tên người dùng không được để trống.")
  private String fullname;

  @NotBlank(message = "Email không được để trống.")
  @UniqueEmail
  private String email;

  @NotBlank(message = "Mật khẩu không được để trống.") private String password;
  @NotBlank(message = "Nhập lại mật khẩu không được để trống.") private String ConfirmPassword;
}
