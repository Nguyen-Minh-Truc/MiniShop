package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.PurchaseOrder;
import com.example.MiniShop.models.request.PurchaseOrderRequest;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.PurchaseOrderResDetail;
import com.example.MiniShop.services.PurchaseOrderService;
import com.example.MiniShop.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {
  private final PurchaseOrderService purchaseOrderSer;

  @GetMapping
  @ApiMessage("Lấy danh sách thành Công.")
  public ResponseEntity<ApiResponsePagination>
  getAllProducts(@Filter Specification<PurchaseOrder> spec, Pageable pageable) {
    return ResponseEntity.ok(purchaseOrderSer.fetchAll(spec, pageable));
  }

  @PostMapping
  public ResponseEntity<PurchaseOrderResDetail>
  createPurchaseOrder(@Valid @RequestBody PurchaseOrderRequest request)
      throws NotFoundException, ConflictException {
    PurchaseOrderResDetail po = purchaseOrderSer.create(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(po);
  }

  @GetMapping("/{id}")
  @ApiMessage("Lấy chi tiết phiếu nhập. ")
  public ResponseEntity<?> getPurchaseOrderById(@PathVariable("id") Long id)
      throws NotFoundException {
    PurchaseOrderResDetail dto = this.purchaseOrderSer.getById(id);
    return ResponseEntity.ok(dto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?>
  updatePurchaseOrder(@PathVariable Long id,
                      @Valid @RequestBody PurchaseOrderRequest req)
      throws NotFoundException, ConflictException {

    return ResponseEntity.ok(this.purchaseOrderSer.update(req, id));
  }

  @PutMapping("/{id}/confirm")
  @ApiMessage("Xác nhận phiếu nhập thành công.")
  public ResponseEntity<PurchaseOrderResDetail>
  confirmPurchaseOrder(@PathVariable("id") Long id)
      throws NotFoundException, ConflictException {
    return ResponseEntity.ok(this.purchaseOrderSer.confirmPurchaseOrder(id));
  }

  @PutMapping("/{id}/cancel")
  @ApiMessage("Hủy phiếu nhập thành công.")
  public ResponseEntity<PurchaseOrderResDetail>
  cancelPurchaseOrder(@PathVariable("id") Long id)
      throws NotFoundException, ConflictException {
    return ResponseEntity.ok(this.purchaseOrderSer.cancelPurchaseOrder(id));
  }

  @PutMapping("/{id}/success")
  @ApiMessage("Hoàn thành phiếu nhập thành công.")
  public ResponseEntity<PurchaseOrderResDetail>
  completePurchaseOrder(@PathVariable("id") Long id)
      throws NotFoundException, ConflictException {
    return ResponseEntity.ok(this.purchaseOrderSer.completePurchaseOrder(id));
  }
}
