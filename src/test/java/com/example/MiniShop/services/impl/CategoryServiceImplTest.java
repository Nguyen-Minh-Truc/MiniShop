package com.example.MiniShop.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.CategoryMapper;
import com.example.MiniShop.models.entity.Category;
import com.example.MiniShop.models.request.CategoryReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.CategoryDetailDto;
import com.example.MiniShop.models.response.CategoryRepDto;
import com.example.MiniShop.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

  @Mock private CategoryRepository categoryRepository;
  @Mock private CategoryMapper categoryMapper;

  @InjectMocks private CategoryServiceImpl categoryService;

  private Category category;
  private CategoryReq categoryReq;
  private CategoryRepDto categoryRepDto;
  private CategoryDetailDto categoryDetailDto;

  @BeforeEach
  void setUp() {
    category = new Category();
    category.setId(1L);
    category.setName("Electronics");

    categoryReq = new CategoryReq();
    categoryReq.setName("Electronics");
    categoryReq.setDescription("Category description");

    categoryRepDto = new CategoryRepDto();
    categoryRepDto.setId(1L);
    categoryRepDto.setName("Electronics");

    categoryDetailDto = new CategoryDetailDto();
    categoryDetailDto.setId(1L);
    categoryDetailDto.setName("Electronics");
  }

  @Nested
  class FetchAllCategoryTests {
    @Test
    void fetchAllCategory_WhenPageHasData_ReturnsPaginationResponse() {
      // Happy path: trả về meta + danh sách dto.
      Specification<Category> spec = (root, query, cb) -> null;
      Pageable pageable = PageRequest.of(0, 5);
      Page<Category> page = new PageImpl<>(List.of(category), pageable, 1);

      when(categoryRepository.findAll(spec, pageable)).thenReturn(page);
      when(categoryMapper.toDtoList(List.of(category)))
          .thenReturn(List.of(categoryRepDto));

      ApiResponsePagination result =
          categoryService.fetchAllCategory(spec, pageable);

      assertThat(result).isNotNull();
      assertThat(result.getMeta().getPageCurrent()).isEqualTo(1);
      assertThat(result.getMeta().getPageSize()).isEqualTo(5);
      assertThat(result.getResult()).isEqualTo(List.of(categoryRepDto));
    }
  }

  @Nested
  class AddCategoryTests {
    @Test
    void addCategory_WhenRequestIsValid_ReturnsSavedCategoryDto() {
      // Happy path: tạo category thành công.
      when(categoryMapper.toEntity(categoryReq)).thenReturn(category);
      when(categoryRepository.save(category)).thenReturn(category);
      when(categoryMapper.toDto(category)).thenReturn(categoryRepDto);

      CategoryRepDto result = categoryService.addCategory(categoryReq);

      assertThat(result).isEqualTo(categoryRepDto);
      verify(categoryRepository, times(1)).save(category);
    }
  }

  @Nested
  class FetchByIdTests {
    @Test
    void fetchById_WhenCategoryNotFound_ThrowsNotFoundException() {
      // Exception case: không tìm thấy category.
      when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> categoryService.fetchById(99L));

      assertThat(thrown.getMessage())
          .isEqualTo("Category not found with id: 99");
    }
  }

  @Nested
  class UpdateCategoryByIDTests {
    @Test
    void updateCategoryByID_WhenCategoryExists_UpdatesAndReturnsDetailDto()
        throws Exception {
      // Happy path: cập nhật category thành công.
      when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
      when(categoryRepository.save(category)).thenReturn(category);
      when(categoryMapper.toCategoryDetailDto(category))
          .thenReturn(categoryDetailDto);

      CategoryDetailDto result =
          categoryService.updateCategoryByID(1L, categoryReq);

      assertThat(result).isEqualTo(categoryDetailDto);
      assertThat(category.getName()).isEqualTo(categoryReq.getName());
      assertThat(category.getDescription())
          .isEqualTo(categoryReq.getDescription());
    }
  }
}
