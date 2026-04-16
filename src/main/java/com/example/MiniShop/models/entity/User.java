package com.example.MiniShop.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

  @Column(columnDefinition = "MEDIUMTEXT") private String refreshToken;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL) private Cart cart;

  @OneToMany(mappedBy = "user")
  private List<Order> orders;

  private LocalDateTime createdAt;

  @ManyToOne @JoinColumn(name = "role_id") private Role role;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
