package com.example.MiniShop.models.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRepDto {

  private Long id;

  private String name;

  private String description;

  private BigDecimal price;

  private int stock;

  private boolean active;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private Long categoryId;

  private String categoryName;

  private Long sellerId;

  private String sellerName;

  private List<String> imageUrls;
}
