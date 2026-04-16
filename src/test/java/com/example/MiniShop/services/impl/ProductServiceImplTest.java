package com.example.MiniShop.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.ProductMapper;
import com.example.MiniShop.models.entity.Category;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.request.CreateProductReq;
import com.example.MiniShop.models.request.UpdateProductReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ProductRepDto;
import com.example.MiniShop.repository.CategoryRepository;
import com.example.MiniShop.repository.ProductImageRepository;
import com.example.MiniShop.repository.ProductRepository;
import com.example.MiniShop.repository.UserRepository;
import java.math.BigDecimal;
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
class ProductServiceImplTest {

  @Mock private ProductRepository productRepository;
  @Mock private ProductMapper productMapper;
  @Mock private ProductImageRepository productImageRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private UserRepository userRepository;
  @Mock private S3ServiceImpl s3ServiceImpl;

  @InjectMocks private ProductServiceImpl productService;

  private Product product;
  private ProductRepDto productRepDto;

  @BeforeEach
  void setUp() {
    product = new Product();
    product.setId(1L);

    productRepDto = new ProductRepDto();
    productRepDto.setId(1L);
  }

  @Nested
  class FetchAllTests {
    @Test
    void fetchAll_WhenPageHasData_ReturnsPaginationResponse() {
      // Happy path: trả về page product.
      Specification<Product> spec = (root, query, cb) -> null;
      Pageable pageable = PageRequest.of(0, 10);
      Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);

      when(productRepository.findAll(spec, pageable)).thenReturn(page);
      when(productMapper.toDtoList(List.of(product)))
          .thenReturn(List.of(productRepDto));

      ApiResponsePagination result = productService.fetchAll(spec, pageable);

      assertThat(result.getMeta().getPageCurrent()).isEqualTo(1);
      assertThat(result.getResult()).isEqualTo(List.of(productRepDto));
    }
  }

  @Nested
  class CreateTests {
    @Test
    void create_WhenCategoryNotFound_ThrowsNotFoundException() {
      // Exception case: category không tồn tại.
      CreateProductReq req = new CreateProductReq();
      req.setCategoryId(99L);

      when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(NotFoundException.class,
                                              () -> productService.create(req));

      assertThat(thrown.getMessage()).isEqualTo("Category not found");
    }
  }

  @Nested
  class GetByIdTests {
    @Test
    void getById_WhenProductNotFound_ThrowsNotFoundException() {
      // Exception case: product không tồn tại.
      when(productRepository.findById(77L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> productService.getById(77L));

      assertThat(thrown.getMessage()).isEqualTo("Product not found");
    }
  }

  @Nested
  class UpdateTests {
    @Test
    void update_WhenProductNotFound_ThrowsNotFoundException() {
      // Exception case: id product update không tồn tại.
      UpdateProductReq req = new UpdateProductReq();
      req.setName("P1");
      req.setDescription("Desc");
      req.setPrice(BigDecimal.TEN);
      req.setCategoryId(1L);

      when(productRepository.findById(1L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> productService.update(1L, req));

      assertThat(thrown.getMessage()).isEqualTo("Product not found");
    }
  }

  @Nested
  class DeleteImageTests {
    @Test
    void deleteImage_WhenImageNotFound_ThrowsNotFoundException() {
      // Exception case: image id không tồn tại.
      when(productImageRepository.findById(5L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> productService.deleteImage(5L));

      assertThat(thrown.getMessage()).isEqualTo("Image không tồn tại");
    }
  }
}
