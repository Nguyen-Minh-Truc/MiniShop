package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.Category;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.ProductImage;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.request.CreateProductReq;
import com.example.MiniShop.models.response.CategoryRepDto;
import com.example.MiniShop.models.response.ProductRepDto;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

  public ProductRepDto toDto(Product product) {
    if (product == null)
      return null;
    ProductRepDto dto = new ProductRepDto();
    dto.setId(product.getId());
    dto.setName(product.getName());
    dto.setDescription(product.getDescription());
    dto.setPrice((product.getPrice()));
    dto.setStock(product.getStock());
    dto.setActive(product.isActive());
    dto.setCreatedAt(product.getCreatedAt());
    dto.setUpdatedAt(product.getUpdatedAt());

    if (product.getCategory() != null) {
      dto.setCategoryId(product.getCategory().getId());
      dto.setCategoryName(product.getCategory().getName());
    }

    if (product.getSeller() != null) {
      dto.setSellerId(product.getSeller().getId());
      dto.setSellerName(product.getSeller().getUsername());
    }

    if (product.getImages() != null && !product.getImages().isEmpty()) {
      dto.setImageUrls(
          product.getImages().stream().map(ProductImage::getImageUrl).toList());
    }
    return dto;
  }

  public List<ProductRepDto> toDtoList(List<Product> products) {
    return products.stream().map(this::toDto).collect(Collectors.toList());
  }

    public Product toEntity(CreateProductReq req, Category category,
                            User seller) {
      Product product = new Product();

      product.setName(req.getName());
      product.setDescription(req.getDescription());
      product.setActive(true);
      product.setCategory(category);
      product.setSeller(seller);

      return product;
    }
}