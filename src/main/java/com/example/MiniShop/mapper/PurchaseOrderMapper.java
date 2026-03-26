package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.PurchaseOrder;
import com.example.MiniShop.models.entity.PurchaseOrderItem;
import com.example.MiniShop.models.entity.Supplier;
import com.example.MiniShop.models.request.PurchaseOrderItemRequest;
import com.example.MiniShop.models.request.PurchaseOrderRequest;
import com.example.MiniShop.models.response.ProductRepDto;
import com.example.MiniShop.models.response.PurchaseOrderResponse;
import com.example.MiniShop.models.response.SupplierRepDto;
import com.example.MiniShop.util.enums.PurchaseOrderStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderMapper {

  public PurchaseOrderResponse toResponse(PurchaseOrder po) {
    if (po == null)
      return null;

    PurchaseOrderResponse res = new PurchaseOrderResponse();

    res.setId(po.getId());
    res.setCreatedBy(po.getCreatedBy());
    res.setCreatedAt(po.getCreatedAt());
    res.setStatus(po.getCompleted());
    res.setTotalPrice(po.getTotalPrice());
    if (po.getSupplier() != null) {
      res.setSupplierName(po.getSupplier().getName());
    }
    return res;
  }

  public List<PurchaseOrderResponse>
  toDtoList(List<PurchaseOrder> purchaseOrders) {
    return purchaseOrders.stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public PurchaseOrder toEntity(PurchaseOrderRequest req, Supplier supplier,
                              List<Product> products) {

    PurchaseOrder po = new PurchaseOrder();
    po.setSupplier(supplier);
    po.setCompleted(PurchaseOrderStatus.PENDING);
    po.setCreatedBy("SYSTEM");

    BigDecimal total = BigDecimal.ZERO;

    List<PurchaseOrderItem> items = new ArrayList<>();

    for (PurchaseOrderItemRequest itemReq : req.getItems()) {

        // 1️⃣ Tìm product tương ứng
        Product product = null;
        for (Product p : products) {
            if (p.getId() == itemReq.getProductId()) {
                product = p;
                break;
            }
        }
        if (product == null) {
            throw new RuntimeException("Product không tồn tại: " + itemReq.getProductId());
        }

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setProduct(product);
        item.setQuantity(itemReq.getQuantity());
        item.setCostPrice(itemReq.getCostPrice());
        item.setPurchaseOrder(po);

        total = total.add(itemReq.getCostPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));

        items.add(item);
    }

    po.setItems(items);
    po.setTotalPrice(total);

    return po;
}
}