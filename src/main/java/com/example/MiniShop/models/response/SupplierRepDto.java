package com.example.MiniShop.models.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierRepDto {
  private long id;

  private String name;

  private String phone;

  private String email;

  private String address;

  private boolean active;
}
