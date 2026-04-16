package com.example.MiniShop.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.InventoryMapper;
import com.example.MiniShop.models.entity.Inventory;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.request.InventoryReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.InventoryDetailDto;
import com.example.MiniShop.models.response.InventoryRepDto;
import com.example.MiniShop.repository.InventoryRepository;
import com.example.MiniShop.repository.ProductRepository;
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
class InventoryServiceImplTest {

  @Mock private InventoryRepository inventoryRepository;
  @Mock private ProductRepository productRepository;
  @Mock private InventoryMapper inventoryMapper;

  @InjectMocks private InventoryServiceImpl inventoryService;

  private InventoryReq req;
  private Inventory inventory;
  private Product product;
  private InventoryRepDto repDto;

  @BeforeEach
  void setUp() {
    req = new InventoryReq();
    req.setProductId(10L);
    req.setStock(10);
    req.setReservedStock(2);

    product = new Product();
    product.setId(10L);

    inventory = new Inventory();
    inventory.setId(1L);
    inventory.setProduct(product);
    inventory.setStock(10);
    inventory.setReserved_stock(2);

    repDto = new InventoryRepDto();
    repDto.setId(1L);
  }

  @Nested
  class FetchAllInventoriesTests {
    @Test
    void fetchAllInventories_WhenPageHasData_ReturnsPaginationResponse() {
      // Happy path: trả về danh sách inventory theo page.
      Specification<Inventory> spec = (root, query, cb) -> null;
      Pageable pageable = PageRequest.of(0, 10);
      Page<Inventory> page = new PageImpl<>(List.of(inventory), pageable, 1);

      when(inventoryRepository.findAll(spec, pageable)).thenReturn(page);
      when(inventoryMapper.toDtoList(List.of(inventory)))
          .thenReturn(List.of(repDto));

      ApiResponsePagination result =
          inventoryService.fetchAllInventories(spec, pageable);

      assertThat(result.getMeta().getPageCurrent()).isEqualTo(1);
      assertThat(result.getResult()).isEqualTo(List.of(repDto));
    }
  }

  @Nested
  class CreateInventoryTests {
    @Test
    void
    createInventory_WhenReservedGreaterThanStock_ThrowsConflictException() {
      // Edge case: reservedStock > stock.
      req.setStock(5);
      req.setReservedStock(6);

      ConflictException thrown = assertThrows(
          ConflictException.class, () -> inventoryService.createInventory(req));

      assertThat(thrown.getMessage())
          .isEqualTo("reservedStock không được lớn hơn stock.");
    }
  }

  @Nested
  class FetchByIdTests {
    @Test
    void fetchById_WhenInventoryNotFound_ThrowsNotFoundException() {
      // Exception case: không tìm thấy inventory.
      when(inventoryRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> inventoryService.fetchById(99L));

      assertThat(thrown.getMessage())
          .isEqualTo("Inventory not found with id: 99");
    }
  }

  @Nested
  class UpdateByIdTests {
    @Test
    void updateById_WhenInventoryNotFound_ThrowsNotFoundException() {
      // Exception case: inventory id không tồn tại.
      when(inventoryRepository.findById(100L)).thenReturn(Optional.empty());

      NotFoundException thrown =
          assertThrows(NotFoundException.class,
                       () -> inventoryService.updateById(100L, req));

      assertThat(thrown.getMessage())
          .isEqualTo("Inventory not found with id: 100");
    }
  }
}
