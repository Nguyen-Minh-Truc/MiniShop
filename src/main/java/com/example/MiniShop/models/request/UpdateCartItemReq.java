package com.example.MiniShop.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartItemReq {
  @NotNull(message = "Số lượng không được null.")
  @Min(value = 1, message = "Số lượng phải lớn hơn 0.")
  private Integer quantity;
}