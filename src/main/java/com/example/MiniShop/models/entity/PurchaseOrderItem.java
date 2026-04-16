package com.example.MiniShop.models.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "import_receipts_items")
@Getter
@Setter
public class PurchaseOrderItem {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

  @ManyToOne
  @JoinColumn(name = "import_receipt_id")
  private PurchaseOrder purchaseOrder;

  @ManyToOne @JoinColumn(name = "product_id") private Product product;

  private int quantity;
  private BigDecimal costPrice;
}