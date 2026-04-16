package com.example.MiniShop.models.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryAvailabilityWsRes {
  private Long productId;
  private int stock;
  private int reservedStock;
  private int availableStock;
  private String eventType;
  private LocalDateTime updatedAt;
}