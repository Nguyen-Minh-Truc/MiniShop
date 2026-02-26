package com.example.MiniShop.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
public class Supplier {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

  @NotBlank(message = "Tên người dùng không được để trống.")
  private String name;

  private String phone;

  private String email;

  private String address;

  private boolean active;
}