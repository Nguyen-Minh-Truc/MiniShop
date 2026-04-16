package com.example.MiniShop.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryReq {

  @NotNull(message = "Số lượng tồn kho không được để trống.")
  @Min(value = 0, message = "Số lượng tồn kho phải >= 0.")
  private Integer stock;

  @NotNull(message = "Số lượng giữ chỗ không được để trống.")
  @Min(value = 0, message = "Số lượng giữ chỗ phải >= 0.")
  private Integer reservedStock;

  @NotNull(message = "Mã sản phẩm không được để trống.")
  @Min(value = 1, message = "Mã sản phẩm phải > 0.")
  private Long productId;
}