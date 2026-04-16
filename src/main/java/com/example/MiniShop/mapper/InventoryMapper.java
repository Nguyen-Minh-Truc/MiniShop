package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.Inventory;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.request.InventoryReq;
import com.example.MiniShop.models.response.InventoryDetailDto;
import com.example.MiniShop.models.response.InventoryRepDto;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

  public Inventory toEntity(InventoryReq req, Product product) {
    if (req == null)
      return null;

    Inventory inventory = new Inventory();
    inventory.setStock(req.getStock());
    inventory.setReserved_stock(req.getReservedStock());
    inventory.setProduct(product);
    return inventory;
  }

  public InventoryRepDto toDto(Inventory inventory) {
    if (inventory == null)
      return null;

    InventoryRepDto dto = new InventoryRepDto();
    dto.setId(inventory.getId());
    dto.setStock(inventory.getStock());
    dto.setReservedStock(inventory.getReserved_stock());

    if (inventory.getProduct() != null) {
      dto.setProductId(inventory.getProduct().getId());
      dto.setProductName(inventory.getProduct().getName());
    }
    return dto;
  }

  public InventoryDetailDto toDetailDto(Inventory inventory) {
    if (inventory == null)
      return null;

    InventoryDetailDto dto = new InventoryDetailDto();
    dto.setId(inventory.getId());
    dto.setStock(inventory.getStock());
    dto.setReservedStock(inventory.getReserved_stock());

    if (inventory.getProduct() != null) {
      dto.setProductId(inventory.getProduct().getId());
      dto.setProductName(inventory.getProduct().getName());
    }
    return dto;
  }

  public List<InventoryRepDto> toDtoList(List<Inventory> inventories) {
    return inventories.stream().map(this::toDto).collect(Collectors.toList());
  }
}