package com.example.MiniShop.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inventories")
public class Inventory {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  private int stock;

  private int reserved_stock;
  @OneToOne
  @JoinColumn(name = "product_id", unique = true)
  private Product product;
}
