package com.example.MiniShop.models.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRes {
  private String accessToken;
  private UserLogin userLogin;

  

  @Getter
  @Setter
  public static class UserLogin {
    private long id;
    private String email;
    private String name;

    public UserLogin(long id, String email, String name) {
      this.id = id;
      this.email = email;
      this.name = name;
    }

    public UserLogin() {}
  }
}