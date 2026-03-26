package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.Category;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.ProductImage;
import com.example.MiniShop.models.request.CategoryReq;
import com.example.MiniShop.models.response.CategoryDetailDto;
import com.example.MiniShop.models.response.CategoryRepDto;
import com.example.MiniShop.models.response.ProductRepDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

  public CategoryRepDto toDto(Category category) {
    if (category == null)
      return null;
    CategoryRepDto dto = new CategoryRepDto();
    dto.setId(category.getId());
    dto.setName(category.getName());
    dto.setDescription(category.getDescription());
    dto.setActive(category.isActive());
    return dto;
  }

  public Category toEntity(CategoryReq req) {
    if (req == null)
      return null;
    Category category = new Category();
    category.setName(req.getName());
    category.setDescription(req.getDescription());
    category.setActive(true);
    return category;
  }

  public CategoryDetailDto toCategoryDetailDto(Category category) {
    if (category == null)
      return null;

    CategoryDetailDto dto = new CategoryDetailDto();
    dto.setId(category.getId());
    dto.setName(category.getName());
    dto.setDescription(category.getDescription());
    dto.setActive(category.isActive());

    // Khởi tạo list
    List<ProductRepDto> productDtos = new ArrayList<>();
    if (category.getProducts() != null) {
      for (Product product : category.getProducts()) {
        if (product != null) {
          ProductRepDto pDto = new ProductRepDto();
          pDto.setId(product.getId());
          pDto.setName(product.getName());
          pDto.setDescription(product.getDescription());
          pDto.setPrice(product.getPrice());
          pDto.setStock(product.getStock());
          pDto.setActive(product.isActive());
          pDto.setCreatedAt(product.getCreatedAt());
          pDto.setUpdatedAt(product.getUpdatedAt());

          if (product.getCategory() != null) {
            pDto.setCategoryId(product.getCategory().getId());
            pDto.setCategoryName(product.getCategory().getName());
          }

          if (product.getSeller() != null) {
            pDto.setSellerId(product.getSeller().getId());
            pDto.setSellerName(product.getSeller().getUsername());
          }
          if (product.getImages() != null && !product.getImages().isEmpty()) {
            pDto.setImageUrls(product.getImages()
                                  .stream()
                                  .map(ProductImage::getImageUrl)
                                  .toList());
          }

          productDtos.add(pDto);
        }
      }
    }

    dto.setProducts(productDtos);

    return dto;
  }
  public List<CategoryRepDto> toDtoList(List<Category> categories) {
    return categories.stream().map(this::toDto).collect(Collectors.toList());
  }
}