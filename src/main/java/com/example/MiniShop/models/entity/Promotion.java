package com.example.MiniShop.models.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "promotions")
@Getter
@Setter
public class Promotion {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  @Column(nullable = false) private String name;

  @Column(nullable = false) private String type;

  @Column(nullable = false) private Double discountValue;

  private LocalDateTime startAt;

  private LocalDateTime endAt;

  private boolean active = true;

  private String code;

  // Áp dụng cho sản phẩm
  @ManyToOne @JoinColumn(name = "product_id") private Product product;

  // Áp dụng cho category
  @ManyToOne @JoinColumn(name = "category_id") private Category category;

  @PrePersist
  public void prePersist() {
    if (startAt == null)
      startAt = LocalDateTime.now();
  }
}