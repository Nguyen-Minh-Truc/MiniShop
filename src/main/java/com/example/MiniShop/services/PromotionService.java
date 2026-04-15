package com.example.MiniShop.services;

import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Promotion;
import com.example.MiniShop.models.request.PromotionReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.PromotionDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PromotionService {
  ApiResponsePagination fetchAll(Specification<Promotion> spec,
                                 Pageable pageable);

  PromotionDto create(PromotionReq promotionReq) throws NotFoundException;

  PromotionDto getById(long id) throws NotFoundException;

  PromotionDto update(long id, PromotionReq promotionReq)
      throws NotFoundException;
}
