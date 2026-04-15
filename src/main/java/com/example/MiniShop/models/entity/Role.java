package com.example.MiniShop.models.entity;

import com.example.MiniShop.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  private String name;

  private String description;

  @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
  @JsonIgnore
  private List<User> user;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  @ManyToMany
  @JsonIgnoreProperties(value = {"roles"})
  @JoinTable(name = "permission_role",
             joinColumns = @JoinColumn(name = "role_id"),
             inverseJoinColumns = @JoinColumn(name = "permission_id"))
  private List<Permission> permissions;

  @PrePersist
  public void prePersist() {
    this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                         ? SecurityUtil.getCurrentUserLogin().get()
                         : null;
    this.createdAt = LocalDateTime.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent()
                         ? SecurityUtil.getCurrentUserLogin().get()
                         : null;

    this.updatedAt = LocalDateTime.now();
  }
}