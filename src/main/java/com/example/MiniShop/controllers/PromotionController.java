package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Promotion;
import com.example.MiniShop.models.request.PromotionReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.PromotionDto;
import com.example.MiniShop.services.PromotionService;
import com.example.MiniShop.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {
  private final PromotionService promotionService;

  @GetMapping
  @ApiMessage("Lấy danh sách khuyến mãi thành công.")
  public ResponseEntity<ApiResponsePagination>
  getAllPromotions(@Filter Specification<Promotion> spec, Pageable pageable) {
    return ResponseEntity.ok(promotionService.fetchAll(spec, pageable));
  }

  @PostMapping
  @ApiMessage("Tạo khuyến mãi thành công.")
  public ResponseEntity<PromotionDto>
  createPromotion(@Valid @RequestBody PromotionReq promotionReq)
      throws NotFoundException {
    PromotionDto dto = promotionService.create(promotionReq);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @GetMapping("/{id}")
  @ApiMessage("Lấy khuyến mãi theo id thành công.")
  public ResponseEntity<PromotionDto>
  getPromotionById(@PathVariable("id") long id) throws NotFoundException {
    PromotionDto dto = promotionService.getById(id);
    return ResponseEntity.ok(dto);
  }

  @PutMapping("/{id}")
  @ApiMessage("Cập nhật khuyến mãi thành công.")
  public ResponseEntity<PromotionDto>
  updatePromotion(@PathVariable("id") long id,
                  @Valid @RequestBody PromotionReq promotionReq)
      throws NotFoundException {
    PromotionDto dto = promotionService.update(id, promotionReq);
    return ResponseEntity.ok(dto);
  }
}
