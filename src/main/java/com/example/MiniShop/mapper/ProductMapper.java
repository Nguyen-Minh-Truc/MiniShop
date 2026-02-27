package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.response.ProductRepDto;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

   public ProductRepDto toDto(Product product) {
    if (product == null) return null;

    ProductRepDto dto = new ProductRepDto();
    dto.setId(product.getId());
    dto.setName(product.getName());
    dto.setDescription(product.getDescription());

   
    dto.setPrice(BigDecimal.valueOf(product.getPrice()));

    dto.setStock(product.getStock());
    dto.setActive(product.isActive());

    if (product.getCategory() != null) {
        dto.setCategoryName(product.getCategory().getName());
    }

    if (product.getSupplier() != null) {
        dto.setSupplierName(product.getSupplier().getName());
    }

    return dto;
}
}