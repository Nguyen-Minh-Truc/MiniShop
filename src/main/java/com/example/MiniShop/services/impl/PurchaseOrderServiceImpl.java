package com.example.MiniShop.services.impl;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.PurchaseOrderMapper;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.PurchaseOrder;
import com.example.MiniShop.models.entity.Supplier;
import com.example.MiniShop.models.request.PurchaseOrderItemRequest;
import com.example.MiniShop.models.request.PurchaseOrderRequest;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ApiResponsePagination.Meta;
import com.example.MiniShop.models.response.PurchaseOrderResponse;
import com.example.MiniShop.repository.ProductRepository;
import com.example.MiniShop.repository.PurchaseOrderRepository;
import com.example.MiniShop.repository.SupplierRepository;
import com.example.MiniShop.services.PurchaseOrderService;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
  private final PurchaseOrderRepository purchaseOrderRepository;
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
public PurchaseOrderResponse create(PurchaseOrderRequest req) throws NotFoundException, ConflictException {

    
    Supplier supplier = supplierRepository.findById(req.getSupplierId())
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
            throw new NotFoundException("Product không tồn tại trong DB, id: " + productId);
        }
    }


    Set<Long> seen = new HashSet<>();
    for (PurchaseOrderItemRequest itemReq : req.getItems()) {
        if (!seen.add(itemReq.getProductId())) {
            throw new ConflictException("Không được nhập trùng product trong đơn: " + itemReq.getProductId());
        }
    }

    PurchaseOrder po = purchaseOrderMapper.toEntity(req, supplier, products);

    purchaseOrderRepository.save(po);

    PurchaseOrderResponse rep = purchaseOrderMapper.toResponse(po);

    return rep;
}
}
