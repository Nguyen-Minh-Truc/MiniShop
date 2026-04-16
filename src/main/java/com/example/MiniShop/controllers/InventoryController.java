package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Inventory;
import com.example.MiniShop.models.request.InventoryReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.InventoryDetailDto;
import com.example.MiniShop.models.response.InventoryRepDto;
import com.example.MiniShop.services.InventoryService;
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
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

  private final InventoryService inventoryService;

  @GetMapping
  @ApiMessage("Lấy danh sách tồn kho thành công.")
  public ResponseEntity<ApiResponsePagination>
  getAllInventories(@Filter Specification<Inventory> spec, Pageable pageable) {
    return ResponseEntity.ok(
        this.inventoryService.fetchAllInventories(spec, pageable));
  }

  @PostMapping
  @ApiMessage("Tạo tồn kho thành công.")
  public ResponseEntity<InventoryRepDto>
  createInventory(@Valid @RequestBody InventoryReq inventoryReq)
      throws NotFoundException, ConflictException {
    InventoryRepDto dto = this.inventoryService.createInventory(inventoryReq);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @GetMapping("/{id}")
  @ApiMessage("Lấy chi tiết tồn kho thành công.")
  public ResponseEntity<InventoryDetailDto>
  getInventoryById(@PathVariable("id") long id) throws NotFoundException {
    InventoryDetailDto dto = this.inventoryService.fetchById(id);
    return ResponseEntity.ok(dto);
  }

  @PutMapping("/{id}")
  @ApiMessage("Cập nhật tồn kho thành công.")
  public ResponseEntity<InventoryDetailDto>
  updateInventoryById(@PathVariable("id") long id,
                      @Valid @RequestBody InventoryReq inventoryReq)
      throws NotFoundException, ConflictException {
    InventoryDetailDto dto = this.inventoryService.updateById(id, inventoryReq);
    return ResponseEntity.ok(dto);
  }
}