package com.example.MiniShop.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PurchaseOrderItemRequest {

    @NotNull(message = "ProductId không được null")
    private Long productId;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private int quantity;

    @NotNull(message = "Giá nhập không được null")
    private BigDecimal costPrice;
}