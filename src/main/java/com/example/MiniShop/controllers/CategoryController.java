package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Category;
import com.example.MiniShop.models.request.CategoryReq;
import com.example.MiniShop.models.response.CategoryDetailDto;
import com.example.MiniShop.models.response.CategoryRepDto;
import com.example.MiniShop.services.impl.CategoryServiceImpl;
import com.example.MiniShop.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
  private final CategoryServiceImpl categoryService;

  @GetMapping
  @ApiMessage("Lấy danh sách thành Công.")
  public ResponseEntity<?> getAllCategories(@Filter Specification<Category> spec, Pageable pageable) {
    return ResponseEntity.ok(categoryService.fetchAllCategory(spec,  pageable));
  }

  @PostMapping
  @ApiMessage("Tạo Loại thành Công.")
  public ResponseEntity<?>
  createCategory(@Valid @RequestBody CategoryReq categoryReq) {
    CategoryRepDto dto = categoryService.addCategory(categoryReq);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @GetMapping("/{id}")
  @ApiMessage("Lấy Loại thành Công.")
  public ResponseEntity<CategoryDetailDto>
  getCategoryById(@PathVariable("id") long id) throws NotFoundException {
    CategoryDetailDto dto = categoryService.fetchById(id);
    return ResponseEntity.ok(dto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> putCategoryById(@PathVariable("id") long id,
                                           @RequestBody CategoryReq categoryReq)
      throws NotFoundException {

    CategoryDetailDto dto = categoryService.updateCategoryByID(id, categoryReq);
    return ResponseEntity.ok(dto);
  }
}
