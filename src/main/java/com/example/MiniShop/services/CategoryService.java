package com.example.MiniShop.services;

import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Category;
import com.example.MiniShop.models.request.CategoryReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.CategoryDetailDto;
import com.example.MiniShop.models.response.CategoryRepDto;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CategoryService {
  ApiResponsePagination fetchAllCategory(Specification<Category> spec, Pageable pageable);
  CategoryRepDto addCategory(CategoryReq categoryReq);
  CategoryDetailDto fetchById(long id) throws NotFoundException;
  CategoryDetailDto updateCategoryByID(long id, CategoryReq categoryReq) throws NotFoundException;
}
