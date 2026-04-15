package com.example.MiniShop.services.impl;

import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.PromotionMapper;
import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.Promotion;
import com.example.MiniShop.models.request.PromotionReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ApiResponsePagination.Meta;
import com.example.MiniShop.models.response.PromotionDto;
import com.example.MiniShop.repository.ProductRepository;
import com.example.MiniShop.repository.PromotionRepository;
import com.example.MiniShop.services.PromotionService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
  private final PromotionRepository promotionRepository;
  private final ProductRepository productRepository;
  private final PromotionMapper promotionMapper;

  @Override
  public ApiResponsePagination fetchAll(Specification<Promotion> spec,
                                        Pageable pageable) {
    Pageable safePageable = Objects.requireNonNull(pageable);
    Page<Promotion> promotions =
        promotionRepository.findAll(spec, safePageable);

    Meta meta = new Meta();
    meta.setPageCurrent(safePageable.getPageNumber() + 1);
    meta.setPageSize(safePageable.getPageSize());
    meta.setPages(promotions.getTotalPages());
    meta.setTotal(promotions.getTotalElements());

    ApiResponsePagination response = new ApiResponsePagination();
    response.setMeta(meta);
    response.setResult(promotionMapper.toDtoList(promotions.getContent()));
    return response;
  }

  @Override
  public PromotionDto create(PromotionReq promotionReq)
      throws NotFoundException {
    
    Promotion promotion =this.promotionMapper.toEntity(promotionReq);

    promotion.setProduct(resolveProduct(promotionReq.getProductId()));


    Promotion savedPromotion = promotionRepository.save(promotion);
    return promotionMapper.toDto(savedPromotion);
  }

  @Override
  public PromotionDto getById(long id) throws NotFoundException {
    Promotion promotion = promotionRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Promotion not found with id: " + id));

    return promotionMapper.toDto(promotion);
  }

  @Override
  public PromotionDto update(long id, PromotionReq promotionReq)
      throws NotFoundException {
    Promotion promotion = promotionRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Promotion not found with id: " + id));

    promotion.setName(promotionReq.getName());
    promotion.setType(promotionReq.getType());
    promotion.setDiscountValue(promotionReq.getDiscountValue());
    promotion.setStartAt(promotionReq.getStartAt());
    promotion.setEndAt(promotionReq.getEndAt());
    promotion.setCode(promotionReq.getCode());
    if (promotionReq.getStatus() != null) {
      promotion.setStatus(promotionReq.getStatus());
    }

    promotion.setProduct(resolveProduct(promotionReq.getProductId()));


    Promotion savedPromotion = promotionRepository.save(promotion);
    return promotionMapper.toDto(savedPromotion);
  }

  private Product resolveProduct(Long productId) throws NotFoundException {
    if (productId == null) {
      return null;
    }

    return productRepository.findById(productId).orElseThrow(
        () -> new NotFoundException("Product not found with id: " + productId));
  }
}
