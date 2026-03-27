package com.example.MiniShop.models.response;

import com.example.MiniShop.models.entity.PurchaseOrderItem;
import com.example.MiniShop.models.entity.Supplier;
import com.example.MiniShop.util.enums.PurchaseOrderStatus;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PurchaseOrderResDetail {
  private long id;

  private String createdBy;

  private LocalDateTime createdAt;

  private PurchaseOrderStatus completed;

  private Supplier supplier;
  private BigDecimal totalPrice;

  private List<PurchaseOrderItemRes> items;
}
