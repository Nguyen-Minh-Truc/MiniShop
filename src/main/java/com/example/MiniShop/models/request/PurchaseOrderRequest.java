package com.example.MiniShop.models.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class PurchaseOrderRequest {

    @NotNull(message = "Supplier không được null")
    private Long supplierId;

    @NotEmpty(message = "Danh sách sản phẩm không được rỗng")
    private List<PurchaseOrderItemRequest> items;

}