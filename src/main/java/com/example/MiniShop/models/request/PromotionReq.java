package com.example.MiniShop.models.request;

import com.example.MiniShop.util.enums.PromotionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionReq {

  @NotBlank(message = "Promotion name is required") private String name;

  @NotBlank(message = "Promotion type is required") private String type;

  @NotNull(message = "Discount value is required") private Double discountValue;

  private LocalDateTime startAt;

  private LocalDateTime endAt;

  private PromotionStatus status;

  private String code;

  private Long productId;

}
