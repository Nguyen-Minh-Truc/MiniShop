package com.example.MiniShop.models.response;

import com.example.MiniShop.models.entity.PurchaseOrderItem;
import com.example.MiniShop.util.enums.PurchaseOrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PurchaseOrderResponse {

    private long id;

    private String createdBy;

    private LocalDateTime createdAt;

    private PurchaseOrderStatus status;

    private String supplierName;
    private BigDecimal totalPrice;
}