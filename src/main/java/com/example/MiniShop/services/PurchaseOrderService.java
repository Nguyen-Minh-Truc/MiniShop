package com.example.MiniShop.services;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.PurchaseOrder;
import com.example.MiniShop.models.request.PurchaseOrderRequest;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.PurchaseOrderResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PurchaseOrderService {
  ApiResponsePagination fetchAll(Specification<PurchaseOrder> specification,
                                 Pageable pageable);

  public PurchaseOrderResponse create(PurchaseOrderRequest req)
      throws NotFoundException,ConflictException;
}
