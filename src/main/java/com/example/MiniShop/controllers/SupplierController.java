package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Supplier;
import com.example.MiniShop.models.request.SupplierReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.SupplierRepDto;
import com.example.MiniShop.services.SupplierService;
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
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {
  private final SupplierService service;

  @PostMapping
  @ApiMessage("Tạo nhà cung cấp thành Công.")
  public ResponseEntity<SupplierRepDto>
  createSupplier(@Valid @RequestBody SupplierReq supplierReq)
      throws ConflictException {
    SupplierRepDto dto = service.createSupplier(supplierReq);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @GetMapping
  @ApiMessage("Lấy tất cả nhà cung cấp. ")
  public ResponseEntity<ApiResponsePagination>
  fetchAll(@Filter Specification<Supplier> spec, Pageable pageable) {
    return ResponseEntity.ok(this.service.fetchAllSuppliers(spec, pageable));
  }

  @GetMapping("/{id}")
  @ApiMessage("Lấy chi tiết nhà cung cấp. ")
  public ResponseEntity<SupplierRepDto>
  getSupplierById(@PathVariable("id") long id) throws NotFoundException {
    SupplierRepDto dto = this.service.fetchById(id);

    return ResponseEntity.ok(dto);
  }

  @PutMapping("/{id}")
  @ApiMessage("Cập nhật nhà cung cấp thành công.")
  public ResponseEntity<SupplierRepDto>
  updateSupplier(@PathVariable long id,
                 @RequestBody @Valid SupplierReq supplierReq)
      throws NotFoundException, ConflictException {
    SupplierRepDto updatedSupplier =
        this.service.updateSupplierByID(id, supplierReq);

    return ResponseEntity.ok(updatedSupplier);
  }
}
