package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.Order;
import com.example.MiniShop.models.entity.OrderDetail;
import com.example.MiniShop.models.response.OrderCheckoutRes;
import com.example.MiniShop.models.response.OrderItemCheckoutRes;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
  public OrderCheckoutRes toCheckoutRes(Order order, List<String> promotionNames) {
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
}