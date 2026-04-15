package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.Promotion;
import com.example.MiniShop.models.request.PromotionReq;
import com.example.MiniShop.models.response.PromotionDto;
import com.example.MiniShop.util.enums.PromotionStatus;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PromotionMapper {

  public PromotionDto toDto(Promotion promotion) {
    if (promotion == null) {
      return null;
    }

    PromotionDto dto = new PromotionDto();
    dto.setId(promotion.getId());
    dto.setName(promotion.getName());
    dto.setType(promotion.getType());
    dto.setDiscountValue(promotion.getDiscountValue());
    dto.setStartAt(promotion.getStartAt());
    dto.setEndAt(promotion.getEndAt());
    dto.setStatus(promotion.getStatus());
    dto.setCode(promotion.getCode());

    if (promotion.getProduct() != null) {
      dto.setProductId(promotion.getProduct().getId());
      dto.setProductName(promotion.getProduct().getName());
    }

    return dto;
  }

  public List<PromotionDto> toDtoList(List<Promotion> promotions) {
    return promotions.stream().map(this::toDto).collect(Collectors.toList());
  }

  public Promotion toEntity(PromotionReq req) {
    if (req == null) {
      return null;
    }

    Promotion promotion = new Promotion();
    promotion.setName(req.getName());
    promotion.setType(req.getType());
    promotion.setDiscountValue(req.getDiscountValue());
    promotion.setStartAt(req.getStartAt());
    promotion.setEndAt(req.getEndAt());
    promotion.setCode(req.getCode());
    promotion.setStatus(PromotionStatus.CREATED);

    return promotion;
  }
}
