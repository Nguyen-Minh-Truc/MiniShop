package com.example.MiniShop.services.impl;

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
import com.example.MiniShop.services.CartService;
import com.example.MiniShop.util.SecurityUtil;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
  private final CartRepository cartRepository;
  private final CartDetailRepository cartDetailRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final CartMapper cartMapper;

  @Override
  @Transactional
  public CartRes getMyCart() throws NotFoundException {
    Cart cart = getCurrentCart();
    return this.cartMapper.toDto(cart);
  }

  @Override
  @Transactional
  public CartRes addItem(AddCartItemReq req)
      throws NotFoundException, ConflictException {
    User user = getCurrentUser();
    Product product = this.productRepository.findById(req.getProductId())
                         .orElseThrow(() -> new NotFoundException(
                             "Product không tồn tại với id: " +
                             req.getProductId()));

    Cart cart = this.cartRepository.findByUserId(user.getId())
                   .orElseGet(() -> {
                     Cart newCart = new Cart();
                     newCart.setUser(user);
                     newCart.setTotalPrice(BigDecimal.ZERO);
                     newCart.setItems(new ArrayList<>());
                     return this.cartRepository.save(newCart);
                   });

    if (cart.getItems() == null) {
      cart.setItems(new ArrayList<>());
    }

    CartDetail detail = this.cartDetailRepository
                            .findByCartIdAndProductId(cart.getId(),
                                                      product.getId())
                            .orElse(null);

    if (detail == null) {
      detail = new CartDetail();
      detail.setCart(cart);
      detail.setProduct(product);
      detail.setQuantity(req.getQuantity());
      detail.setPrice(product.getPrice());
      cart.getItems().add(detail);
    } else {
      detail.setQuantity(detail.getQuantity() + req.getQuantity());
      detail.setPrice(product.getPrice());
    }

    recalculateTotal(cart);
    Cart savedCart = this.cartRepository.save(cart);
    return this.cartMapper.toDto(savedCart);
  }

  @Override
  @Transactional
  public CartRes updateItem(Long itemId, UpdateCartItemReq req)
      throws NotFoundException, ConflictException {
    User user = getCurrentUser();
    Cart cart = this.cartRepository.findByUserId(user.getId())
                   .orElseThrow(() -> new NotFoundException(
                       "Giỏ hàng không tồn tại."));

    CartDetail detail = this.cartDetailRepository
                            .findByIdAndCartId(itemId, cart.getId())
                            .orElseThrow(() -> new NotFoundException(
                                "Cart item không tồn tại."));

    if (req.getQuantity() <= 0) {
      throw new ConflictException("Số lượng phải lớn hơn 0.");
    }

    detail.setQuantity(req.getQuantity());
    if (detail.getProduct() != null) {
      detail.setPrice(detail.getProduct().getPrice());
    }

    recalculateTotal(cart);
    Cart savedCart = this.cartRepository.save(cart);
    return this.cartMapper.toDto(savedCart);
  }

  @Override
  @Transactional
  public void deleteItem(Long itemId) throws NotFoundException {
    User user = getCurrentUser();
    Cart cart = this.cartRepository.findByUserId(user.getId())
                   .orElseThrow(() -> new NotFoundException(
                       "Giỏ hàng không tồn tại."));

    CartDetail detail = this.cartDetailRepository
                            .findByIdAndCartId(itemId, cart.getId())
                            .orElseThrow(() -> new NotFoundException(
                                "Cart item không tồn tại."));

    cart.getItems().remove(detail);

    if (cart.getItems().isEmpty()) {
      this.cartRepository.delete(cart);
      return;
    }

    recalculateTotal(cart);
    this.cartRepository.save(cart);
  }

  @Override
  @Transactional
  public void deleteMyCart() throws NotFoundException {
    User user = getCurrentUser();
    Cart cart = this.cartRepository.findByUserId(user.getId())
                   .orElseThrow(() -> new NotFoundException(
                       "Giỏ hàng không tồn tại."));
    this.cartRepository.delete(cart);
  }

  private User getCurrentUser() throws NotFoundException {
    String email = SecurityUtil.getCurrentUserLogin()
                       .orElseThrow(() -> new NotFoundException(
                           "Không tìm thấy thông tin đăng nhập."));

    User user = this.userRepository.findByEmail(email);
    if (user == null) {
      throw new NotFoundException("Người dùng không tồn tại.");
    }
    return user;
  }

  private Cart getCurrentCart() throws NotFoundException {
    User user = getCurrentUser();
    return this.cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new NotFoundException("Giỏ hàng không tồn tại."));
  }

  private void recalculateTotal(Cart cart) {
    BigDecimal total = BigDecimal.ZERO;
    List<CartDetail> items = cart.getItems();
    if (items != null) {
      for (CartDetail item : items) {
        if (item.getPrice() != null) {
          total = total.add(
              item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
      }
    }
    cart.setTotalPrice(total);
  }
}