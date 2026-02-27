package com.example.MiniShop.models.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRepDto {
  private long id;
  private String name;
  private String description;
  private BigDecimal price;
  private Integer stock;
  private boolean active;
  private String categoryName;
  private String supplierName;
}
