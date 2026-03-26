package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.PurchaseOrder;
import com.example.MiniShop.models.request.PurchaseOrderRequest;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.PurchaseOrderResponse;
import com.example.MiniShop.services.impl.PurchaseOrderServiceImpl;
import com.example.MiniShop.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {
  private final PurchaseOrderServiceImpl purchaseOrderSer;

  @GetMapping
  @ApiMessage("Lấy danh sách thành Công.")
  public ResponseEntity<ApiResponsePagination>
  getAllProducts(@Filter Specification<PurchaseOrder> spec, Pageable pageable) {
    return ResponseEntity.ok(purchaseOrderSer.fetchAll(spec, pageable));
  }

  @PostMapping
  public ResponseEntity<PurchaseOrderResponse>
  createPurchaseOrder(@Valid @RequestBody PurchaseOrderRequest request) throws NotFoundException, ConflictException {

    // 1️⃣ Gọi service tạo entity
    PurchaseOrderResponse po = purchaseOrderSer.create(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(po);
  }
}
