package com.example.MiniShop.models.response;

import com.example.MiniShop.models.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRes {
  @JsonProperty("access_token") private String accessToken;
  private UserLogin userLogin;

  @Getter
  @Setter
  public static class UserLogin {
    private long id;
    private String email;
    private String name;
    private Role role;

    public UserLogin(long id, String email, String name, Role role) {
      this.id = id;
      this.email = email;
      this.name = name;
      this.role = role;
    }

    public UserLogin() {}
  }
}