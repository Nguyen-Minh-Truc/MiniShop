package com.example.MiniShop.models.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

  private String name;

  private String description;

  private BigDecimal price;

  private int stock;

  private boolean active = true;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @ManyToOne @JoinColumn(name = "category_id") private Category category;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
  private List<ProductImage> images;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
  private List<Promotion> promotions;

  @ManyToOne
  @JoinColumn(name = "seller_id", nullable = false)
  private User seller;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}