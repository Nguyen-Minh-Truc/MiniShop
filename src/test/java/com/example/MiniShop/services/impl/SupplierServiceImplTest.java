package com.example.MiniShop.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.SupplierMapper;
import com.example.MiniShop.models.entity.Supplier;
import com.example.MiniShop.models.request.SupplierReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.SupplierRepDto;
import com.example.MiniShop.repository.SupplierRepository;
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
class SupplierServiceImplTest {

  @Mock private SupplierRepository supplierRepository;
  @Mock private SupplierMapper supplierMapper;

  @InjectMocks private SupplierServiceImpl supplierService;

  private SupplierReq req;
  private Supplier supplier;
  private SupplierRepDto dto;

  @BeforeEach
  void setUp() {
    req = new SupplierReq();
    req.setName("ABC Supplier");
    req.setEmail("abc@example.com");
    req.setPhone("0900000000");
    req.setAddress("HCM");

    supplier = new Supplier();
    supplier.setId(1L);
    supplier.setName("ABC Supplier");
    supplier.setEmail("abc@example.com");

    dto = new SupplierRepDto();
    dto.setId(1L);
    dto.setName("ABC Supplier");
    dto.setEmail("abc@example.com");
  }

  @Nested
  class CreateSupplierTests {
    @Test
    void createSupplier_WhenEmailAlreadyExists_ThrowsConflictException() {
      // Exception case: email nhà cung cấp trùng.
      when(supplierRepository.existsByEmail(req.getEmail())).thenReturn(true);

      ConflictException thrown = assertThrows(
          ConflictException.class, () -> supplierService.createSupplier(req));

      assertThat(thrown.getMessage())
          .isEqualTo("Email nhà cung cấp đã tồn tại.");
    }
  }

  @Nested
  class FetchAllSuppliersTests {
    @Test
    void fetchAllSuppliers_WhenPageHasData_ReturnsPaginationResponse() {
      // Happy path: trả về page suppliers.
      Specification<Supplier> spec = (root, query, cb) -> null;
      Pageable pageable = PageRequest.of(0, 5);
      Page<Supplier> page = new PageImpl<>(List.of(supplier), pageable, 1);

      when(supplierRepository.findAll(spec, pageable)).thenReturn(page);
      when(supplierMapper.toDtoList(List.of(supplier)))
          .thenReturn(List.of(dto));

      ApiResponsePagination result =
          supplierService.fetchAllSuppliers(spec, pageable);

      assertThat(result.getMeta().getPageCurrent()).isEqualTo(1);
      assertThat(result.getResult()).isEqualTo(List.of(dto));
    }
  }

  @Nested
  class FetchByIdTests {
    @Test
    void fetchById_WhenSupplierNotFound_ThrowsNotFoundException() {
      // Exception case: supplier không tồn tại.
      when(supplierRepository.findById(88L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> supplierService.fetchById(88L));

      assertThat(thrown.getMessage())
          .isEqualTo("Supplier not found with id: 88");
    }
  }

  @Nested
  class UpdateSupplierByIDTests {
    @Test
    void
    updateSupplierByID_WhenEmailBelongsToAnotherSupplier_ThrowsConflictException() {
      // Exception case: email đã thuộc supplier khác.
      Supplier existing = new Supplier();
      existing.setId(2L);

      when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
      when(supplierRepository.findByEmail(req.getEmail()))
          .thenReturn(Optional.of(existing));

      ConflictException thrown =
          assertThrows(ConflictException.class,
                       () -> supplierService.updateSupplierByID(1L, req));

      assertThat(thrown.getMessage())
          .isEqualTo("Email nhà cung cấp đã tồn tại.");
    }
  }
}
