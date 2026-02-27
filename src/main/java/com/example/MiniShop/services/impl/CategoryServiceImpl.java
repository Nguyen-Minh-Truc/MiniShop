package com.example.MiniShop.services.impl;

import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.CategoryMapper;
import com.example.MiniShop.models.entity.Category;
import com.example.MiniShop.models.request.CategoryReq;
import com.example.MiniShop.models.response.ApiResponsePagination.Meta;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.CategoryDetailDto;
import com.example.MiniShop.models.response.CategoryRepDto;
import com.example.MiniShop.repository.CategoryRepository;
import com.example.MiniShop.services.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public ApiResponsePagination fetchAllCategory(Specification<Category> spec,
                                               Pageable pageable) {
    Page<Category> categories = categoryRepository.findAll(spec,pageable);

    Meta meta = new Meta();

    meta.setPageCurrent(pageable.getPageNumber() + 1);
    meta.setPageSize(pageable.getPageSize());

    meta.setPages(categories.getTotalPages());
    meta.setTotal(categories.getTotalElements());

    ApiResponsePagination apiResponsePagination = new ApiResponsePagination();

    apiResponsePagination.setMeta(meta);


    apiResponsePagination.setResult(categoryMapper.toDtoList(categories.getContent()));


    return apiResponsePagination;
  }

  public CategoryRepDto addCategory(CategoryReq categoryReq) {
    Category category = categoryMapper.toEntity(categoryReq);
    Category savedCategory = categoryRepository.save(category);
    return categoryMapper.toDto(savedCategory);
  }

  public CategoryDetailDto fetchById(long id) throws NotFoundException {
    Category category = categoryRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Category not found with id: " + id));

    return categoryMapper.toCategoryDetailDto(category);
  }

  @Override
  public CategoryDetailDto updateCategoryByID(long id, CategoryReq categoryReq)
      throws NotFoundException {
    Category category = categoryRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Category not found with id: " + id));

    category.setName(categoryReq.getName());
    category.setDescription(categoryReq.getDescription());
    return this.categoryMapper.toCategoryDetailDto(
        this.categoryRepository.save(category));
  }
}
