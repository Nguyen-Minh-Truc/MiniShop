package com.example.MiniShop.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.CartMapper;
import com.example.MiniShop.models.entity.Cart;
import com.example.MiniShop.models.entity.CartDetail;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.request.AddCartItemReq;
import com.example.MiniShop.models.request.UpdateCartItemReq;
import com.example.MiniShop.models.response.CartRes;
import com.example.MiniShop.repository.CartDetailRepository;
import com.example.MiniShop.repository.CartRepository;
import com.example.MiniShop.repository.ProductRepository;
import com.example.MiniShop.repository.UserRepository;
import com.example.MiniShop.util.SecurityUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
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

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

  @Mock private CartRepository cartRepository;
  @Mock private CartDetailRepository cartDetailRepository;
  @Mock private ProductRepository productRepository;
  @Mock private UserRepository userRepository;
  @Mock private CartMapper cartMapper;

  @InjectMocks private CartServiceImpl cartService;

  private User user;
  private Cart cart;
  private Product product;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setEmail("user@example.com");

    product = new Product();
    product.setId(10L);
    product.setName("Keyboard");
    product.setPrice(BigDecimal.valueOf(100));

    cart = new Cart();
    cart.setId(2L);
    cart.setUser(user);
    cart.setItems(new ArrayList<>());
    cart.setTotalPrice(BigDecimal.ZERO);
  }

  @Nested
  class GetMyCartTests {
    @Test
    void getMyCart_WhenCartExists_ReturnsCartDto() throws Exception {
      // Happy path: lấy giỏ hàng hiện tại thành công.
      CartRes cartRes = new CartRes();
      cartRes.setId(2L);

      try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
        mocked.when(SecurityUtil::getCurrentUserLogin)
            .thenReturn(Optional.of("user@example.com"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartMapper.toDto(cart)).thenReturn(cartRes);

        CartRes result = cartService.getMyCart();

        assertThat(result).isEqualTo(cartRes);
      }
    }
  }

  @Nested
  class AddItemTests {
    @Test
    void addItem_WhenProductNotFound_ThrowsNotFoundException() {
      // Exception case: product không tồn tại.
      AddCartItemReq req = new AddCartItemReq();
      req.setProductId(99L);
      req.setQuantity(1);

      try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
        mocked.when(SecurityUtil::getCurrentUserLogin)
            .thenReturn(Optional.of("user@example.com"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                                                () -> cartService.addItem(req));

        assertThat(thrown.getMessage())
            .contains("Product không tồn tại với id: 99");
      }
    }
  }

  @Nested
  class UpdateItemTests {
    @Test
    void updateItem_WhenQuantityIsZero_ThrowsConflictException() {
      // Edge case: quantity <= 0.
      UpdateCartItemReq req = new UpdateCartItemReq();
      req.setQuantity(0);

      CartDetail detail = new CartDetail();
      detail.setId(5L);
      detail.setCart(cart);
      detail.setProduct(product);

      try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
        mocked.when(SecurityUtil::getCurrentUserLogin)
            .thenReturn(Optional.of("user@example.com"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartDetailRepository.findByIdAndCartId(5L, 2L))
            .thenReturn(Optional.of(detail));

        ConflictException thrown = assertThrows(
            ConflictException.class, () -> cartService.updateItem(5L, req));

        assertThat(thrown.getMessage()).isEqualTo("Số lượng phải lớn hơn 0.");
      }
    }
  }

  @Nested
  class DeleteItemTests {
    @Test
    void deleteItem_WhenItemIsLastItem_DeletesCart() throws Exception {
      // Happy path: xóa item cuối cùng thì xóa luôn cart.
      CartDetail detail = new CartDetail();
      detail.setId(8L);
      detail.setCart(cart);
      cart.setItems(new ArrayList<>(List.of(detail)));

      try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
        mocked.when(SecurityUtil::getCurrentUserLogin)
            .thenReturn(Optional.of("user@example.com"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartDetailRepository.findByIdAndCartId(8L, 2L))
            .thenReturn(Optional.of(detail));

        cartService.deleteItem(8L);

        verify(cartRepository, times(1)).delete(cart);
      }
    }
  }

  @Nested
  class DeleteMyCartTests {
    @Test
    void deleteMyCart_WhenCartExists_DeletesCart() throws Exception {
      // Happy path: xóa toàn bộ giỏ hàng.
      try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
        mocked.when(SecurityUtil::getCurrentUserLogin)
            .thenReturn(Optional.of("user@example.com"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        cartService.deleteMyCart();

        verify(cartRepository, times(1)).delete(cart);
      }
    }
  }
}
