package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.Order;
import com.example.MiniShop.models.entity.OrderDetail;
import com.example.MiniShop.models.response.OrderCheckoutRes;
import com.example.MiniShop.models.response.OrderItemCheckoutRes;
import com.example.MiniShop.models.response.OrderItemRes;
import com.example.MiniShop.models.response.OrderRes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
  public OrderCheckoutRes toCheckoutRes(Order order,
                                        List<String> promotionNames) {
    if (order == null) {
      return null;
    }

    OrderCheckoutRes res = new OrderCheckoutRes();
    res.setId(order.getId());
    res.setStatus(order.getStatus());
    res.setTotalPrice(order.getTotalPrice());
    res.setExpiredAt(order.getExpiredAt());

    if (order.getUser() != null) {
      res.setUserId(order.getUser().getId());
    }

    List<OrderItemCheckoutRes> itemRes = new ArrayList<>();
    if (order.getItems() != null) {
      for (int i = 0; i < order.getItems().size(); i++) {
        OrderDetail item = order.getItems().get(i);
        OrderItemCheckoutRes dto = new OrderItemCheckoutRes();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getPrice());
        dto.setItemTotal(item.getTotalPrice());
        dto.setProductName(item.getProductName());
        if (item.getProduct() != null) {
          dto.setProductId(item.getProduct().getId());
        }
        if (promotionNames != null && i < promotionNames.size()) {
          dto.setPromotionName(promotionNames.get(i));
        }
        itemRes.add(dto);
      }
    }
    res.setItems(itemRes);
    return res;
  }

  public OrderRes toDto(Order order) {
    if (order == null) {
      return null;
    }

    OrderRes res = new OrderRes();
    res.setId(order.getId());
    res.setStatus(order.getStatus());
    res.setTotalPrice(order.getTotalPrice());
    res.setCreatedAt(order.getCreatedAt());
    res.setUpdatedAt(order.getUpdatedAt());
    res.setExpiredAt(order.getExpiredAt());
    res.setShippingAddress(order.getShipping_address());
    res.setShippingPhone(order.getShipping_Phone());
    res.setMethodPayment(order.getMethod_payment());

    if (order.getUser() != null) {
      res.setUserId(order.getUser().getId());
      res.setUsername(order.getUser().getUsername());
    }

    if (order.getItems() != null) {
      res.setItems(toItemDtoList(order.getItems()));
    }

    return res;
  }

  public List<OrderRes> toDtoList(List<Order> orders) {
    return orders.stream().map(this::toDto).collect(Collectors.toList());
  }

  public List<OrderItemRes> toItemDtoList(List<OrderDetail> items) {
    return items.stream().map(this::toItemDto).collect(Collectors.toList());
  }

  public OrderItemRes toItemDto(OrderDetail item) {
    OrderItemRes res = new OrderItemRes();
    res.setId(item.getId());
    res.setQuantity(item.getQuantity());
    res.setUnitPrice(item.getPrice());
    res.setItemTotal(item.getTotalPrice());
    res.setProductName(item.getProductName());
    if (item.getProduct() != null) {
      res.setProductId(item.getProduct().getId());
    }
    return res;
  }
}