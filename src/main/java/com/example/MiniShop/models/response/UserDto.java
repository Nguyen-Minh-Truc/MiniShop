package com.example.MiniShop.models.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
  private long id;
  private String username;
  private String email;
  private String address;
  private String phone;
  private boolean active;
  private LocalDateTime createdAt;
}
