package com.example.MiniShop.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.PromotionMapper;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.Promotion;
import com.example.MiniShop.models.request.PromotionReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.PromotionDto;
import com.example.MiniShop.repository.ProductRepository;
import com.example.MiniShop.repository.PromotionRepository;
import java.time.LocalDateTime;
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
class PromotionServiceImplTest {

  @Mock private PromotionRepository promotionRepository;
  @Mock private ProductRepository productRepository;
  @Mock private PromotionMapper promotionMapper;

  @InjectMocks private PromotionServiceImpl promotionService;

  private PromotionReq req;
  private Promotion promotion;
  private PromotionDto dto;

  @BeforeEach
  void setUp() {
    req = new PromotionReq();
    req.setName("Summer Sale");
    req.setType("PERCENT");
    req.setDiscountValue(10.0);
    req.setCode("SUMMER10");
    req.setStartAt(LocalDateTime.now().minusDays(1));
    req.setEndAt(LocalDateTime.now().plusDays(1));

    promotion = new Promotion();
    promotion.setId(1L);
    promotion.setName("Summer Sale");

    dto = new PromotionDto();
    dto.setId(1L);
    dto.setName("Summer Sale");
  }

  @Nested
  class FetchAllTests {
    @Test
    void fetchAll_WhenPageHasData_ReturnsPaginationResponse() {
      // Happy path: trả về danh sách promotion theo page.
      Specification<Promotion> spec = (root, query, cb) -> null;
      Pageable pageable = PageRequest.of(0, 5);
      Page<Promotion> page = new PageImpl<>(List.of(promotion), pageable, 1);

      when(promotionRepository.findAll(spec, pageable)).thenReturn(page);
      when(promotionMapper.toDtoList(List.of(promotion)))
          .thenReturn(List.of(dto));

      ApiResponsePagination result = promotionService.fetchAll(spec, pageable);

      assertThat(result.getMeta().getPageCurrent()).isEqualTo(1);
      assertThat(result.getResult()).isEqualTo(List.of(dto));
    }
  }

  @Nested
  class CreateTests {
    @Test
    void create_WhenProductIdIsNull_CreatesPromotionWithoutProduct()
        throws Exception {
      // Edge case: productId null thì promotion vẫn được tạo.
      req.setProductId(null);
      when(promotionMapper.toEntity(req)).thenReturn(promotion);
      when(promotionRepository.save(promotion)).thenReturn(promotion);
      when(promotionMapper.toDto(promotion)).thenReturn(dto);

      PromotionDto result = promotionService.create(req);

      assertThat(result).isEqualTo(dto);
      assertThat(promotion.getProduct()).isNull();
    }
  }

  @Nested
  class GetByIdTests {
    @Test
    void getById_WhenPromotionNotFound_ThrowsNotFoundException() {
      // Exception case: không tìm thấy promotion.
      when(promotionRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> promotionService.getById(99L));

      assertThat(thrown.getMessage())
          .isEqualTo("Promotion not found with id: 99");
    }
  }

  @Nested
  class UpdateTests {
    @Test
    void update_WhenProductNotFound_ThrowsNotFoundException() {
      // Exception case: update có productId nhưng product không tồn tại.
      req.setProductId(10L);
      when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));
      when(productRepository.findById(10L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> promotionService.update(1L, req));

      assertThat(thrown.getMessage())
          .isEqualTo("Product not found with id: 10");
    }
  }
}
