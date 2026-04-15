package com.example.MiniShop.models.entity;

import com.example.MiniShop.util.enums.PromotionStatus;
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

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PromotionStatus status = PromotionStatus.CREATED;

  private String code;

  @ManyToOne @JoinColumn(name = "product_id") private Product product;

  @PrePersist
  public void prePersist() {
    if (startAt == null)
      startAt = LocalDateTime.now();

    if (status == null)
      status = PromotionStatus.CREATED;
  }
}