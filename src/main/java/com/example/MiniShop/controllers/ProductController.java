package com.example.MiniShop.controllers;

import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.request.CreateProductReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ProductRepDto;
import com.example.MiniShop.services.impl.ProductServiceImpl;
import com.example.MiniShop.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
  private final ProductServiceImpl productSer;
  @GetMapping
  @ApiMessage("Lấy danh sách thành Công.")
  public ResponseEntity<ApiResponsePagination>
  getAllProducts(@Filter Specification<Product> spec, Pageable pageable) {
    return ResponseEntity.ok(productSer.fetchAll(spec, pageable));
  }

  @PostMapping
  public ResponseEntity<ProductRepDto>
  createProduct(@Valid @RequestBody CreateProductReq req) {

    ProductRepDto response = productSer.create(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
