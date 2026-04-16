package com.example.MiniShop.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.OrderMapper;
import com.example.MiniShop.models.entity.Order;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.request.OrderPaymentReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.OrderRes;
import com.example.MiniShop.repository.CartRepository;
import com.example.MiniShop.repository.InventoryRepository;
import com.example.MiniShop.repository.OrderRepository;
import com.example.MiniShop.repository.PromotionRepository;
import com.example.MiniShop.repository.UserRepository;
import com.example.MiniShop.util.SecurityUtil;
import com.example.MiniShop.util.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

  @Mock private CartRepository cartRepository;
  @Mock private InventoryRepository inventoryRepository;
  @Mock private PromotionRepository promotionRepository;
  @Mock private OrderRepository orderRepository;
  @Mock private UserRepository userRepository;
  @Mock private OrderMapper orderMapper;
  @Mock private InventoryRealtimeNotifier inventoryRealtimeNotifier;

  @InjectMocks private OrderServiceImpl orderService;

  private User user;
  private Order order;
  private OrderRes orderRes;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setEmail("user@example.com");

    order = new Order();
    order.setId(10L);
    order.setStatus(OrderStatus.PENDING);

    orderRes = new OrderRes();
    orderRes.setId(10L);
  }

  @Nested
  class CheckoutFromMyCartTests {
    @Test
    void checkoutFromMyCart_WhenCartNotFound_ThrowsConflictException() {
      // Exception case: user có login nhưng chưa có cart.
      try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
        mocked.when(SecurityUtil::getCurrentUserLogin)
            .thenReturn(Optional.of("user@example.com"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        ConflictException thrown = assertThrows(
            ConflictException.class, () -> orderService.checkoutFromMyCart());

        assertThat(thrown.getMessage()).isEqualTo("Giỏ hàng không tồn tại.");
      }
    }
  }

  @Nested
  class PayOrderTests {
    @Test
    void payOrder_WhenOrderStatusIsNotPending_ThrowsConflictException() {
      // Exception case: chỉ PENDING mới thanh toán được.
      order.setStatus(OrderStatus.SHIPPING);
      OrderPaymentReq req = new OrderPaymentReq();
      req.setShippingAddress("HCM");
      req.setShippingPhone("0900");
      req.setPaymentMethod("cash");

      when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

      ConflictException thrown = assertThrows(
          ConflictException.class, () -> orderService.payOrder(10L, req));

      assertThat(thrown.getMessage())
          .isEqualTo("Chỉ đơn hàng PENDING mới được thanh toán.");
    }
  }

  @Nested
  class GetMyOrdersTests {
    @Test
    void getMyOrders_WhenUserAndOrdersExist_ReturnsPaginationResponse()
        throws Exception {
      // Happy path: trả về orders của user hiện tại.
      Pageable pageable = PageRequest.of(0, 5);
      Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

      try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
        mocked.when(SecurityUtil::getCurrentUserLogin)
            .thenReturn(Optional.of("user@example.com"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(orderRepository.findAllByUserId(1L, pageable)).thenReturn(page);
        when(orderMapper.toDtoList(List.of(order)))
            .thenReturn(List.of(orderRes));

        ApiResponsePagination result = orderService.getMyOrders(pageable);

        assertThat(result.getMeta().getPageCurrent()).isEqualTo(1);
        assertThat(result.getResult()).isEqualTo(List.of(orderRes));
      }
    }
  }

  @Nested
  class GetAllOrdersTests {
    @Test
    void getAllOrders_WhenPageHasData_ReturnsPaginationResponse() {
      // Happy path: admin lấy toàn bộ orders.
      Pageable pageable = PageRequest.of(0, 5);
      Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

      when(orderRepository.findAll(pageable)).thenReturn(page);
      when(orderMapper.toDtoList(List.of(order))).thenReturn(List.of(orderRes));

      ApiResponsePagination result = orderService.getAllOrders(pageable);

      assertThat(result.getMeta().getPageCurrent()).isEqualTo(1);
      assertThat(result.getResult()).isEqualTo(List.of(orderRes));
    }
  }

  @Nested
  class GetOrderDetailTests {
    @Test
    void getOrderDetail_WhenOrderNotFound_ThrowsNotFoundException() {
      // Exception case: order không tồn tại.
      when(orderRepository.findById(999L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> orderService.getOrderDetail(999L));

      assertThat(thrown.getMessage()).isEqualTo("Đơn hàng không tồn tại.");
    }
  }

  @Nested
  class CancelOrderTests {
    @Test
    void cancelOrder_WhenOrderStatusIsNotPending_ThrowsConflictException() {
      // Exception case: chỉ PENDING mới hủy được.
      order.setStatus(OrderStatus.SHIPPING);
      when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

      ConflictException thrown = assertThrows(
          ConflictException.class, () -> orderService.cancelOrder(10L));

      assertThat(thrown.getMessage())
          .isEqualTo("Chỉ đơn hàng PENDING mới được hủy.");
    }
  }

  @Nested
  class MarkOrderSuccessTests {
    @Test
    void
    markOrderSuccess_WhenOrderStatusIsNotShipping_ThrowsConflictException() {
      // Exception case: chỉ SHIPPING mới complete được.
      order.setStatus(OrderStatus.PENDING);
      when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

      ConflictException thrown = assertThrows(
          ConflictException.class, () -> orderService.markOrderSuccess(10L));

      assertThat(thrown.getMessage())
          .isEqualTo("Chỉ đơn hàng SHIPPING mới được hoàn tất.");
    }
  }

  @Nested
  class AutoCancelExpiredPendingOrdersTests {
    @Test
    void
    autoCancelExpiredPendingOrders_WhenExpiredOrdersExist_UpdatesStatusToCancelled() {
      // Happy path: auto-cancel order hết hạn pending.
      Order expired = new Order();
      expired.setId(200L);
      expired.setStatus(OrderStatus.PENDING);
      expired.setExpiredAt(LocalDateTime.now().minusMinutes(1));
      expired.setItems(List.of());

      when(orderRepository.findAllByStatusAndExpiredAtLessThanEqual(any(),
                                                                    any()))
          .thenReturn(List.of(expired));

      orderService.autoCancelExpiredPendingOrders();

      assertThat(expired.getStatus()).isEqualTo(OrderStatus.CANCELLED);
      verify(orderRepository, times(1)).save(expired);
    }
  }
}
