package com.example.MiniShop.models.entity;

import com.example.MiniShop.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class Permission {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  private String name;

  private String apiPath;

  private String method;

  private String module;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  @ManyToMany(mappedBy = "permissions") @JsonIgnore private List<Role> roles;

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