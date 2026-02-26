package com.example.MiniShop.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
public class PurchaseOrder {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

  private String createdBy;

  private LocalDateTime createdAt;

  private boolean completed = false;

  @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
  private List<PurchaseOrderItem> items;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}