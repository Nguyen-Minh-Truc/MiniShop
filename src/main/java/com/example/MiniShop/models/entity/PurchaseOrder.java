package com.example.MiniShop.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "import_receipts")
@Getter
@Setter
public class PurchaseOrder {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

  private String createdBy;

  private LocalDateTime createdAt;

  private boolean completed = false;

   @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

  @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
  private List<PurchaseOrderItem> items;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}