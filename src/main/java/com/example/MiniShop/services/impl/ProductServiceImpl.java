package com.example.MiniShop.services.impl;

import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.ProductMapper;
import com.example.MiniShop.models.entity.Category;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.ProductImage;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.request.CreateProductReq;
import com.example.MiniShop.models.request.UpdateProductReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ApiResponsePagination.Meta;
import com.example.MiniShop.models.response.ProductRepDto;
import com.example.MiniShop.repository.CategoryRepository;
import com.example.MiniShop.repository.ProductImageRepository;
import com.example.MiniShop.repository.ProductRepository;
import com.example.MiniShop.repository.UserRepository;
import com.example.MiniShop.services.ProductService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;
  private final ProductImageRepository productImageRepository;
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;
  private final S3ServiceImpl s3ServiceImpl;

  @Override
  public ApiResponsePagination fetchAll(Specification<Product> spec,
                                        Pageable pageable) {
    Page<Product> products = this.productRepository.findAll(spec, pageable);
    Meta meta = new Meta();

    meta.setPageCurrent(pageable.getPageNumber() + 1);
    meta.setPageSize(pageable.getPageSize());
    meta.setPages(products.getTotalPages());
    meta.setTotal(products.getTotalElements());

    ApiResponsePagination apiResponsePagination = new ApiResponsePagination();

    apiResponsePagination.setMeta(meta);
    apiResponsePagination.setResult(
        productMapper.toDtoList(products.getContent()));

    return apiResponsePagination;
  }

  @Transactional
  @Override
  public ProductRepDto create(CreateProductReq req) throws NotFoundException {

    // 1. Validate
    Category category =
        categoryRepository.findById(req.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Category not found"));

    User seller =
        userRepository.findById(req.getSellerId())
            .orElseThrow(() -> new NotFoundException("Seller not found"));

    // 2. Create product
    Product product = this.productMapper.toEntity(req, category, seller);

    product = productRepository.save(product);

    // 3. Save images
    List<ProductImage> images = new ArrayList<>();

    if (req.getImageUrls() != null) {
      for (String url : req.getImageUrls()) {
        ProductImage img = new ProductImage();
        img.setImageUrl(url);
        img.setProduct(product);
        images.add(img);
      }
    }

    productImageRepository.saveAll(images);
    // 4. Map DTO
    ProductRepDto dto = productMapper.toDto(product);
    dto.setImageUrls(images.stream().map(ProductImage::getImageUrl).toList());

    return dto;
  }

  @Override
  public ProductRepDto getById(long id) throws NotFoundException {

    Product product = productRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Product not found"));

    ProductRepDto dto = this.productMapper.toDto(product);
    return dto;
  }

  @Transactional
  @Override
  public ProductRepDto update(Long id, UpdateProductReq req)
      throws NotFoundException {

    Product product = productRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Product not found"));

    Category category =
        categoryRepository.findById(req.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Category not found"));

    product.setName(req.getName());
    product.setDescription(req.getDescription());
    product.setStock(req.getStock());
    product.setPrice(req.getPrice());
    product.setCategory(category);

    product.setUpdatedAt(LocalDateTime.now());
    product = productRepository.save(product);

    ProductRepDto dto = productMapper.toDto(product);

    return dto;
  }

  @Transactional
  public void deleteImage(Long imageId) throws Exception {

    ProductImage img = productImageRepository.findById(imageId).orElseThrow(
        () -> new NotFoundException("Image không tồn tại"));

    s3ServiceImpl.deleteFileByUrl(img.getImageUrl());

    Product product = img.getProduct();
    if (product != null && product.getImages() != null) {
      product.getImages().remove(img);
    }
    this.productImageRepository.delete(img);
  }
}
