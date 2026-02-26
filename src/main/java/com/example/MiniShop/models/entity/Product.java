package com.example.MiniShop.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  @NotBlank(message = "Tên sản phẩm không được để trống.") private String name;

  @NotBlank(message = "Mô tả sản phẩm không được để trống.")
  private String description;

  private Double price;

  private Integer stock;

  private boolean active = true;

  private LocalDateTime createdAt;

  @ManyToOne @JoinColumn(name = "category_id") private Category category;

  @ManyToOne @JoinColumn(name = "supplier_id") private Supplier supplier;

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