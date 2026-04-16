package com.example.MiniShop.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCartItemReq {
  @NotNull(message = "Product không được null.")
  private Long productId;

  @NotNull(message = "Số lượng không được null.")
  @Min(value = 1, message = "Số lượng phải lớn hơn 0.")
  private Integer quantity;
}