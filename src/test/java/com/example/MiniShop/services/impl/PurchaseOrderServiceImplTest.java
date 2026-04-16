package com.example.MiniShop.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.PurchaseOrderMapper;
import com.example.MiniShop.models.entity.PurchaseOrder;
import com.example.MiniShop.models.entity.Supplier;
import com.example.MiniShop.models.request.PurchaseOrderItemRequest;
import com.example.MiniShop.models.request.PurchaseOrderRequest;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.PurchaseOrderResDetail;
import com.example.MiniShop.repository.InventoryRepository;
import com.example.MiniShop.repository.ProductRepository;
import com.example.MiniShop.repository.PurchaseOrderItemRepository;
import com.example.MiniShop.repository.PurchaseOrderRepository;
import com.example.MiniShop.repository.SupplierRepository;
import com.example.MiniShop.util.enums.PurchaseOrderStatus;
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
class PurchaseOrderServiceImplTest {

  @Mock private PurchaseOrderRepository purchaseOrderRepository;
  @Mock private PurchaseOrderItemRepository purchaseOrderItemRepository;
  @Mock private PurchaseOrderMapper purchaseOrderMapper;
  @Mock private SupplierRepository supplierRepository;
  @Mock private ProductRepository productRepository;
  @Mock private InventoryRepository inventoryRepository;

  @InjectMocks private PurchaseOrderServiceImpl purchaseOrderService;

  private PurchaseOrderRequest req;
  private PurchaseOrder purchaseOrder;
  private PurchaseOrderResDetail detail;

  @BeforeEach
  void setUp() {
    PurchaseOrderItemRequest itemReq = new PurchaseOrderItemRequest();
    itemReq.setId(null);
    itemReq.setProductId(10L);
    itemReq.setQuantity(2);
    itemReq.setCostPrice(BigDecimal.valueOf(100));

    req = new PurchaseOrderRequest();
    req.setSupplierId(1L);
    req.setItems(List.of(itemReq));

    purchaseOrder = new PurchaseOrder();
    purchaseOrder.setId(1L);
    purchaseOrder.setStatus(PurchaseOrderStatus.PENDING);
    purchaseOrder.setItems(List.of());

    detail = new PurchaseOrderResDetail();
    detail.setId(1L);
  }

  @Nested
  class FetchAllTests {
    @Test
    void fetchAll_WhenPageHasData_ReturnsPaginationResponse() {
      // Happy path: trả về danh sách phiếu nhập theo page.
      Specification<PurchaseOrder> spec = (root, query, cb) -> null;
      Pageable pageable = PageRequest.of(0, 10);
      Page<PurchaseOrder> page =
          new PageImpl<>(List.of(purchaseOrder), pageable, 1);

      when(purchaseOrderRepository.findAll(spec, pageable)).thenReturn(page);
      when(purchaseOrderMapper.toDtoList(List.of(purchaseOrder)))
          .thenReturn(List.of());

      ApiResponsePagination result =
          purchaseOrderService.fetchAll(spec, pageable);

      assertThat(result.getMeta().getPageCurrent()).isEqualTo(1);
    }
  }

  @Nested
  class CreateTests {
    @Test
    void create_WhenSupplierNotFound_ThrowsNotFoundException() {
      // Exception case: supplier không tồn tại.
      when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> purchaseOrderService.create(req));

      assertThat(thrown.getMessage()).isEqualTo("Supplier không tồn tại");
    }
  }

  @Nested
  class GetByIdTests {
    @Test
    void getById_WhenPurchaseOrderNotFound_ThrowsNotFoundException() {
      // Exception case: phiếu nhập không tồn tại.
      when(purchaseOrderRepository.findById(55L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> purchaseOrderService.getById(55L));

      assertThat(thrown.getMessage()).isEqualTo("Phiếu nhập không tồn tại.");
    }
  }

  @Nested
  class UpdateTests {
    @Test
    void update_WhenStatusIsNotPending_ThrowsConflictException() {
      // Exception case: phiếu nhập đã không còn pending.
      purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
      when(purchaseOrderRepository.findById(1L))
          .thenReturn(Optional.of(purchaseOrder));

      ConflictException thrown = assertThrows(
          ConflictException.class, () -> purchaseOrderService.update(req, 1L));

      assertThat(thrown.getMessage())
          .isEqualTo("Phiếu nhập đã hoàn thành, không thể chỉnh sửa.");
    }
  }

  @Nested
  class ConfirmPurchaseOrderTests {
    @Test
    void confirmPurchaseOrder_WhenStatusIsNotPending_ThrowsConflictException() {
      // Exception case: chỉ pending mới confirm được.
      purchaseOrder.setStatus(PurchaseOrderStatus.CANCELED);
      when(purchaseOrderRepository.findById(1L))
          .thenReturn(Optional.of(purchaseOrder));

      ConflictException thrown =
          assertThrows(ConflictException.class,
                       () -> purchaseOrderService.confirmPurchaseOrder(1L));

      assertThat(thrown.getMessage())
          .isEqualTo("Chỉ phiếu nhập đang PENDING mới được xác nhận.");
    }
  }

  @Nested
  class CancelPurchaseOrderTests {
    @Test
    void cancelPurchaseOrder_WhenStatusIsNotPending_ThrowsConflictException() {
      // Exception case: chỉ pending mới cancel được.
      purchaseOrder.setStatus(PurchaseOrderStatus.SUCCESS);
      when(purchaseOrderRepository.findById(1L))
          .thenReturn(Optional.of(purchaseOrder));

      ConflictException thrown =
          assertThrows(ConflictException.class,
                       () -> purchaseOrderService.cancelPurchaseOrder(1L));

      assertThat(thrown.getMessage())
          .isEqualTo("Chỉ phiếu nhập đang PENDING mới được hủy.");
    }
  }

  @Nested
  class CompletePurchaseOrderTests {
    @Test
    void
    completePurchaseOrder_WhenStatusIsNotConfirmed_ThrowsConflictException() {
      // Exception case: chỉ confirmed mới complete được.
      purchaseOrder.setStatus(PurchaseOrderStatus.PENDING);
      when(purchaseOrderRepository.findById(1L))
          .thenReturn(Optional.of(purchaseOrder));

      ConflictException thrown =
          assertThrows(ConflictException.class,
                       () -> purchaseOrderService.completePurchaseOrder(1L));

      assertThat(thrown.getMessage())
          .isEqualTo("Chỉ phiếu nhập đã được xác nhận mới được hoàn thành.");
    }
  }
}
