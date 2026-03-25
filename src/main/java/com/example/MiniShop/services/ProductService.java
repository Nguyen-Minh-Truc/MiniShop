package com.example.MiniShop.services;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.request.CreateProductReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ProductRepDto;

public interface ProductService {
      ApiResponsePagination fetchAll(Specification<Product> spec, Pageable pageable);
      ProductRepDto create(CreateProductReq productReq);
}
