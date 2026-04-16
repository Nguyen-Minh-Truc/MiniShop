package com.example.MiniShop.services;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Inventory;
import com.example.MiniShop.models.request.InventoryReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.InventoryDetailDto;
import com.example.MiniShop.models.response.InventoryRepDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface InventoryService {
  ApiResponsePagination fetchAllInventories(Specification<Inventory> spec,
                                            Pageable pageable);

  InventoryRepDto createInventory(InventoryReq inventoryReq)
      throws NotFoundException, ConflictException;

  InventoryDetailDto fetchById(long id) throws NotFoundException;

  InventoryDetailDto updateById(long id, InventoryReq inventoryReq)
      throws NotFoundException, ConflictException;
}