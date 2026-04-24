package com.example.MiniShop.services.impl;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.InventoryMapper;
import com.example.MiniShop.models.entity.Inventory;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.request.InventoryReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ApiResponsePagination.Meta;
import com.example.MiniShop.models.response.InventoryDetailDto;
import com.example.MiniShop.models.response.InventoryRepDto;
import com.example.MiniShop.repository.InventoryRepository;
import com.example.MiniShop.repository.ProductRepository;
import com.example.MiniShop.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

  private final InventoryRepository inventoryRepository;
  private final ProductRepository productRepository;
  private final InventoryMapper inventoryMapper;
  private final InventoryRealtimeNotifier inventoryRealtimeNotifier;
  private final DashboardRealtimeNotifier dashboardRealtimeNotifier;

  @Override
  public ApiResponsePagination
  fetchAllInventories(Specification<Inventory> spec, Pageable pageable) {
    Page<Inventory> inventories =
        this.inventoryRepository.findAll(spec, pageable);

    Meta meta = new Meta();
    meta.setPageCurrent(pageable.getPageNumber() + 1);
    meta.setPageSize(pageable.getPageSize());
    meta.setPages(inventories.getTotalPages());
    meta.setTotal(inventories.getTotalElements());

    ApiResponsePagination response = new ApiResponsePagination();
    response.setMeta(meta);
    response.setResult(
        this.inventoryMapper.toDtoList(inventories.getContent()));
    return response;
  }

  @Override
  public InventoryRepDto createInventory(InventoryReq inventoryReq)
      throws NotFoundException, ConflictException {
    validateQuantity(inventoryReq.getStock(), inventoryReq.getReservedStock());

    Product product =
        this.productRepository.findById(inventoryReq.getProductId())
            .orElseThrow(
                ()
                    -> new NotFoundException("Product not found with id: " +
                                             inventoryReq.getProductId()));

    if (this.inventoryRepository.existsByProductId(
            inventoryReq.getProductId())) {
      throw new ConflictException("Sản phẩm này đã có tồn kho.");
    }

    Inventory savedInventory = this.inventoryRepository.save(
        this.inventoryMapper.toEntity(inventoryReq, product));
    inventoryRealtimeNotifier.notifyAvailability(product.getId(),
                           savedInventory,
                           "INVENTORY_CREATED");
    dashboardRealtimeNotifier.publishAll("INVENTORY_CREATED");

    return this.inventoryMapper.toDto(savedInventory);
  }

  @Override
  public InventoryDetailDto fetchById(long id) throws NotFoundException {
    Inventory inventory = this.inventoryRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Inventory not found with id: " + id));
    return this.inventoryMapper.toDetailDto(inventory);
  }

  @Override
  public InventoryDetailDto updateById(long id, InventoryReq inventoryReq)
      throws NotFoundException, ConflictException {
    validateQuantity(inventoryReq.getStock(), inventoryReq.getReservedStock());

    Inventory inventory = this.inventoryRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Inventory not found with id: " + id));

    Product product =
        this.productRepository.findById(inventoryReq.getProductId())
            .orElseThrow(
                ()
                    -> new NotFoundException("Product not found with id: " +
                                             inventoryReq.getProductId()));

    if (inventory.getProduct() != null &&
        inventory.getProduct().getId() != inventoryReq.getProductId() &&
        this.inventoryRepository.existsByProductId(
            inventoryReq.getProductId())) {
      throw new ConflictException("Sản phẩm này đã có tồn kho.");
    }

    inventory.setStock(inventoryReq.getStock());
    inventory.setReserved_stock(inventoryReq.getReservedStock());
    inventory.setProduct(product);

    Inventory updatedInventory = this.inventoryRepository.save(inventory);
    inventoryRealtimeNotifier.notifyAvailability(product.getId(),
                                                 updatedInventory,
                                                 "INVENTORY_UPDATED");
    dashboardRealtimeNotifier.publishAll("INVENTORY_UPDATED");
    return this.inventoryMapper.toDetailDto(updatedInventory);
  }

  private void validateQuantity(int stock, int reservedStock)
      throws ConflictException {
    if (reservedStock > stock) {
      throw new ConflictException("reservedStock không được lớn hơn stock.");
    }
  }
}