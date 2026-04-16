package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.Cart;
import com.example.MiniShop.models.entity.CartDetail;
import com.example.MiniShop.models.response.CartItemRes;
import com.example.MiniShop.models.response.CartRes;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

  public CartRes toDto(Cart cart) {
    if (cart == null) {
      return null;
    }

    CartRes res = new CartRes();
    res.setId(cart.getId());
    res.setTotalPrice(cart.getTotalPrice());

    if (cart.getUser() != null) {
      res.setUserId(cart.getUser().getId());
      res.setUsername(cart.getUser().getUsername());
    }

    if (cart.getItems() != null) {
      res.setItems(toItemDtoList(cart.getItems()));
    }

    return res;
  }

  public CartItemRes toItemDto(CartDetail detail) {
    if (detail == null) {
      return null;
    }

    CartItemRes res = new CartItemRes();
    res.setId(detail.getId());
    res.setQuantity(detail.getQuantity());
    res.setPrice(detail.getPrice());

    if (detail.getProduct() != null) {
      res.setProductId(detail.getProduct().getId());
      res.setProductName(detail.getProduct().getName());
    }

    if (detail.getPrice() != null) {
      res.setTotalPrice(detail.getPrice().multiply(
          BigDecimal.valueOf(detail.getQuantity())));
    }

    return res;
  }

  public List<CartItemRes> toItemDtoList(List<CartDetail> items) {
    return items.stream().map(this::toItemDto).collect(Collectors.toList());
  }
}