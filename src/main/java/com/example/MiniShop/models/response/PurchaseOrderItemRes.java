package com.example.MiniShop.models.response;

import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.PurchaseOrder;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PurchaseOrderItemRes {
  private long id;
  private ProductRepDto product;
  private int quantity;
  private BigDecimal costPrice;
}
