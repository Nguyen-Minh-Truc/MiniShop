package com.example.MiniShop.services.impl;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.PurchaseOrderMapper;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.PurchaseOrder;
import com.example.MiniShop.models.entity.PurchaseOrderItem;
import com.example.MiniShop.models.entity.Supplier;
import com.example.MiniShop.models.request.PurchaseOrderItemRequest;
import com.example.MiniShop.models.request.PurchaseOrderRequest;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ApiResponsePagination.Meta;
import com.example.MiniShop.models.response.PurchaseOrderResDetail;
import com.example.MiniShop.models.response.PurchaseOrderResponse;
import com.example.MiniShop.repository.ProductRepository;
import com.example.MiniShop.repository.PurchaseOrderItemRepository;
import com.example.MiniShop.repository.PurchaseOrderRepository;
import com.example.MiniShop.repository.SupplierRepository;
import com.example.MiniShop.services.PurchaseOrderService;
import com.example.MiniShop.util.enums.PurchaseOrderStatus;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
  private final PurchaseOrderRepository purchaseOrderRepository;
  private final PurchaseOrderItemRepository purchaseOrderItemRepository;
  private final PurchaseOrderMapper purchaseOrderMapper;
  private final SupplierRepository supplierRepository;
  private final ProductRepository productRepository;

  @Override
  public ApiResponsePagination
  fetchAll(Specification<PurchaseOrder> specification, Pageable pageable) {
    Page<PurchaseOrder> purchaseOrder =
        this.purchaseOrderRepository.findAll(specification, pageable);
    Meta meta = new Meta();

    meta.setPageCurrent(pageable.getPageNumber() + 1);
    meta.setPageSize(pageable.getPageSize());
    meta.setPages(purchaseOrder.getTotalPages());
    meta.setTotal(purchaseOrder.getTotalElements());

    ApiResponsePagination apiResponsePagination = new ApiResponsePagination();

    apiResponsePagination.setMeta(meta);
    apiResponsePagination.setResult(
        purchaseOrderMapper.toDtoList(purchaseOrder.getContent()));

    return apiResponsePagination;
  }

  @Transactional
  public PurchaseOrderResDetail create(PurchaseOrderRequest req)
      throws NotFoundException, ConflictException {

    Supplier supplier =
        supplierRepository.findById(req.getSupplierId())
            .orElseThrow(() -> new NotFoundException("Supplier không tồn tại"));

    List<Long> productIds = new ArrayList<>();
    for (PurchaseOrderItemRequest itemReq : req.getItems()) {
      productIds.add(itemReq.getProductId());
    }

    List<Product> products = productRepository.findAllById(productIds);

    for (Long productId : productIds) {
      boolean exists = false;
      for (Product product : products) {
        if (product.getId() == productId) {
          exists = true;
          break;
        }
      }
      if (!exists) {
        throw new NotFoundException("Product không tồn tại trong DB, id: " +
                                    productId);
      }
    }

    Set<Long> seen = new HashSet<>();
    for (PurchaseOrderItemRequest itemReq : req.getItems()) {
      if (!seen.add(itemReq.getProductId())) {
        throw new ConflictException(
            "Không được nhập trùng product trong đơn: " +
            itemReq.getProductId());
      }
    }

    PurchaseOrder po = purchaseOrderMapper.toEntity(req, supplier, products);

    purchaseOrderRepository.save(po);

    PurchaseOrderResDetail rep = purchaseOrderMapper.toDtoDetail(po);

    return rep;
  }

  @Override
  public PurchaseOrderResDetail getById(Long id) throws NotFoundException {
    PurchaseOrder po = this.purchaseOrderRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Phiếu nhập không tồn tại."));
    return this.purchaseOrderMapper.toDtoDetail(po);
  }

  @Override
  @Transactional
  public PurchaseOrderResDetail update(PurchaseOrderRequest req, Long id)
      throws NotFoundException, ConflictException {

    PurchaseOrder po = purchaseOrderRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Phiếu nhập không tồn tại."));

    if (po.getCompleted() != PurchaseOrderStatus.PENDING) {
      throw new ConflictException(
          "Phiếu nhập đã hoàn thành, không thể chỉnh sửa.");
    }

    // Supplier
    Supplier supplier =
        supplierRepository.findById(req.getSupplierId())
            .orElseThrow(() -> new NotFoundException("Supplier không tồn tại"));
    po.setSupplier(supplier);

    // ====== MAP ITEM CŨ ======
    Map<Long, PurchaseOrderItem> existingItemMap =
        po.getItems().stream().collect(
            Collectors.toMap(PurchaseOrderItem::getId, i -> i));

    // ====== LẤY PRODUCT ======
    List<Long> productIds = req.getItems()
                                .stream()
                                .map(PurchaseOrderItemRequest::getProductId)
                                .distinct()
                                .toList();

    Map<Long, Product> productMap =
        productRepository.findAllById(productIds)
            .stream()
            .collect(Collectors.toMap(Product::getId, p -> p));

    BigDecimal total = BigDecimal.ZERO;
    List<PurchaseOrderItem> finalItems = new ArrayList<>();

    // ====== XỬ LÝ CREATE + UPDATE ======
    for (PurchaseOrderItemRequest itemReq : req.getItems()) {

      if (itemReq.getQuantity() <= 0) {
        throw new ConflictException("Quantity phải > 0");
      }

      if (itemReq.getCostPrice() == null ||
          itemReq.getCostPrice().compareTo(BigDecimal.ZERO) <= 0) {
        throw new ConflictException("CostPrice phải > 0");
      }

      Product product = productMap.get(itemReq.getProductId());
      if (product == null) {
        throw new NotFoundException("Product không tồn tại");
      }

      PurchaseOrderItem item;

      // ===== UPDATE =====
      if (itemReq.getId() != null) {
        item = existingItemMap.get(itemReq.getId());

        if (item == null) {
          throw new NotFoundException("Item không tồn tại: " + itemReq.getId());
        }

        item.setProduct(product);
        item.setQuantity(itemReq.getQuantity());
        item.setCostPrice(itemReq.getCostPrice());

        // remove khỏi map → để biết cái nào bị xoá
        existingItemMap.remove(itemReq.getId());

      } else {
        // ===== CREATE =====
        item = new PurchaseOrderItem();
        item.setPurchaseOrder(po);
        item.setProduct(product);
        item.setQuantity(itemReq.getQuantity());
        item.setCostPrice(itemReq.getCostPrice());
      }

      BigDecimal itemTotal = itemReq.getCostPrice().multiply(
          BigDecimal.valueOf(itemReq.getQuantity()));

      total = total.add(itemTotal);
      finalItems.add(item);
    }

    // ===== DELETE =====
    // còn lại trong map là item không còn trong request
    for (PurchaseOrderItem itemToDelete : existingItemMap.values()) {
      purchaseOrderItemRepository.delete(itemToDelete);
    }

    po.setItems(finalItems);
    po.setTotalPrice(total);

    purchaseOrderRepository.save(po);

    return purchaseOrderMapper.toDtoDetail(po);
  }
}