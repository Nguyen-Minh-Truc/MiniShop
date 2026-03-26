package com.example.MiniShop.models.entity;

import com.example.MiniShop.util.enums.PurchaseOrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
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

  private PurchaseOrderStatus completed;

  @ManyToOne @JoinColumn(name = "supplier_id") private Supplier supplier;

  @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
  private List<PurchaseOrderItem> items;

  private BigDecimal totalPrice;
  
  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}