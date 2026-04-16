package com.example.MiniShop.services.impl;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.OrderMapper;
import com.example.MiniShop.models.entity.Cart;
import com.example.MiniShop.models.entity.CartDetail;
import com.example.MiniShop.models.entity.Inventory;
import com.example.MiniShop.models.entity.Order;
import com.example.MiniShop.models.entity.OrderDetail;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.Promotion;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.response.OrderCheckoutRes;
import com.example.MiniShop.repository.CartRepository;
import com.example.MiniShop.repository.InventoryRepository;
import com.example.MiniShop.repository.OrderRepository;
import com.example.MiniShop.repository.PromotionRepository;
import com.example.MiniShop.repository.UserRepository;
import com.example.MiniShop.services.OrderService;
import com.example.MiniShop.util.SecurityUtil;
import com.example.MiniShop.util.enums.OrderStatus;
import com.example.MiniShop.util.enums.PromotionStatus;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
  private final CartRepository cartRepository;
  private final InventoryRepository inventoryRepository;
  private final PromotionRepository promotionRepository;
  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final OrderMapper orderMapper;
  private final InventoryRealtimeNotifier inventoryRealtimeNotifier;

  @Override
  @Transactional
  public OrderCheckoutRes checkoutFromMyCart()
      throws NotFoundException, ConflictException {
    User user = getCurrentUser();
    Cart cart = cartRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ConflictException(
                        "Giỏ hàng không tồn tại."));

    if (cart.getItems() == null || cart.getItems().isEmpty()) {
      throw new ConflictException("Giỏ hàng đang trống.");
    }

    Order order = new Order();
    order.setUser(user);
    order.setStatus(OrderStatus.PENDING);
    order.setExpiredAt(LocalDateTime.now().plusMinutes(10));

    List<OrderDetail> orderItems = new ArrayList<>();
    List<String> promotionNames = new ArrayList<>();
    BigDecimal orderTotal = BigDecimal.ZERO;
    LocalDateTime now = LocalDateTime.now();

    for (CartDetail cartItem : cart.getItems()) {
      Product product = cartItem.getProduct();
      if (product == null) {
        throw new NotFoundException("Sản phẩm trong giỏ hàng không tồn tại.");
      }
      if (cartItem.getQuantity() <= 0) {
        throw new ConflictException("Số lượng sản phẩm phải lớn hơn 0.");
      }

      Inventory inventory = inventoryRepository.findByProductId(product.getId())
                                .orElseThrow(() -> new NotFoundException(
                                    "Sản phẩm chưa có tồn kho: " +
                                    product.getName()));

      int available = inventory.getStock() - inventory.getReserved_stock();
      if (available < cartItem.getQuantity()) {
        throw new ConflictException("Không đủ tồn kho cho sản phẩm: " +
                                    product.getName());
      }

      PromotionResult promotionResult = getBestPromotion(product.getId(), now);
      BigDecimal basePrice = product.getPrice() == null
                                 ? BigDecimal.ZERO
                                 : product.getPrice();
      BigDecimal finalUnitPrice = applyPromotion(basePrice, promotionResult);
      BigDecimal lineTotal =
          finalUnitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

      OrderDetail orderDetail = new OrderDetail();
      orderDetail.setOrder(order);
      orderDetail.setProduct(product);
      orderDetail.setProductName(product.getName());
      orderDetail.setQuantity(cartItem.getQuantity());
      orderDetail.setPrice(finalUnitPrice);
      orderDetail.setTotalPrice(lineTotal);
      orderItems.add(orderDetail);
      promotionNames.add(promotionResult.name);

      inventory.setReserved_stock(inventory.getReserved_stock() +
                                  cartItem.getQuantity());
      inventoryRepository.save(inventory);
      inventoryRealtimeNotifier.notifyAvailability(product.getId(), inventory,
                      "RESERVED");

      orderTotal = orderTotal.add(lineTotal);
    }

    order.setItems(orderItems);
    order.setTotalPrice(orderTotal);
    Order savedOrder = orderRepository.save(order);

    cartRepository.delete(cart);
    return orderMapper.toCheckoutRes(savedOrder, promotionNames);
  }

  @Override
  @Transactional
  public void autoCancelExpiredPendingOrders() {
    List<Order> expiredOrders = orderRepository.findAllByStatusAndExpiredAtLessThanEqual(
        OrderStatus.PENDING, LocalDateTime.now());

    for (Order order : expiredOrders) {
      if (order.getItems() != null) {
        for (OrderDetail detail : order.getItems()) {
          if (detail.getProduct() == null) {
            continue;
          }
          inventoryRepository.findByProductId(detail.getProduct().getId())
              .ifPresent(inventory -> {
                int rollback = Math.max(0,
                    inventory.getReserved_stock() - detail.getQuantity());
                inventory.setReserved_stock(rollback);
                inventoryRepository.save(inventory);
                inventoryRealtimeNotifier.notifyAvailability(
                    detail.getProduct().getId(), inventory, "RELEASED");
              });
        }
      }
      order.setStatus(OrderStatus.CANCELLED);
      orderRepository.save(order);
    }
  }

  private User getCurrentUser() throws NotFoundException {
    String email = SecurityUtil.getCurrentUserLogin()
                       .orElseThrow(() -> new NotFoundException(
                           "Không tìm thấy thông tin đăng nhập."));

    User user = userRepository.findByEmail(email);
    if (user == null) {
      throw new NotFoundException("Người dùng không tồn tại.");
    }
    return user;
  }

  private PromotionResult getBestPromotion(Long productId, LocalDateTime now) {
    List<Promotion> promotions =
        promotionRepository.findAllByProductIdAndStatusAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
            productId, PromotionStatus.ACTIVE, now, now);

    PromotionResult result = new PromotionResult();
    result.discountedPrice = null;
    result.name = null;

    for (Promotion promotion : promotions) {
      if (promotion == null || promotion.getDiscountValue() == null) {
        continue;
      }
      if (result.discountedPrice == null) {
        result.discountedPrice = BigDecimal.valueOf(Double.MAX_VALUE);
      }
      result.promotions.add(promotion);
    }

    return result;
  }

  private BigDecimal applyPromotion(BigDecimal basePrice, PromotionResult promotionResult) {
    if (promotionResult.promotions.isEmpty()) {
      return basePrice;
    }

    BigDecimal best = basePrice;
    String bestName = null;

    for (Promotion promotion : promotionResult.promotions) {
      BigDecimal candidate = basePrice;
      String type = promotion.getType() == null
                        ? ""
                        : promotion.getType().trim().toUpperCase();
      BigDecimal discount = BigDecimal.valueOf(promotion.getDiscountValue());

      if (type.contains("PERCENT")) {
        candidate = basePrice.multiply(BigDecimal.ONE.subtract(
            discount.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)));
      } else {
        candidate = basePrice.subtract(discount);
      }

      if (candidate.compareTo(BigDecimal.ZERO) < 0) {
        candidate = BigDecimal.ZERO;
      }

      if (candidate.compareTo(best) < 0) {
        best = candidate;
        bestName = promotion.getName();
      }
    }

    promotionResult.name = bestName;
    return best;
  }

  private static class PromotionResult {
    private final List<Promotion> promotions = new ArrayList<>();
    private BigDecimal discountedPrice;
    private String name;
  }
}