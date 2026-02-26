package com.example.MiniShop.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

  @NotBlank(message = "Tên người dùng không được để trống.")
  private String username;

  @NotBlank(message = "Email không được để trống.") private String email;

  @NotBlank(message = "Mật khẩu không được để trống.") private String password;

  private String address;

  private String phone;

  private boolean active;

  @OneToMany(mappedBy = "seller", fetch = FetchType.EAGER)
  private List<Product> products;

  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
