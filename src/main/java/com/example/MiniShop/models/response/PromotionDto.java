package com.example.MiniShop.models.response;

import com.example.MiniShop.util.enums.PromotionStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionDto {
  private Long id;
  private String name;
  private String type;
  private Double discountValue;
  private LocalDateTime startAt;
  private LocalDateTime endAt;
  private PromotionStatus status;
  private String code;
  private Long productId;
  private String productName;
}
