package com.example.MiniShop.services;

import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.request.CreateProductReq;
import com.example.MiniShop.models.request.UpdateProductReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ProductRepDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProductService {
  ApiResponsePagination fetchAll(Specification<Product> spec,
                                 Pageable pageable);
  ProductRepDto create(CreateProductReq productReq) throws NotFoundException;
  ProductRepDto getById(long id) throws NotFoundException;
  ProductRepDto update(Long id, UpdateProductReq req) throws NotFoundException;
}
