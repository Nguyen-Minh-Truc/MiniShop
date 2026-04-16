package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.request.AddCartItemReq;
import com.example.MiniShop.models.request.UpdateCartItemReq;
import com.example.MiniShop.models.response.CartRes;
import com.example.MiniShop.services.CartService;
import com.example.MiniShop.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {
  private final CartService cartService;

  @GetMapping("/me")
  @ApiMessage("Lấy giỏ hàng thành công.")
  public ResponseEntity<CartRes> getMyCart() throws NotFoundException {
    return ResponseEntity.ok(this.cartService.getMyCart());
  }

  @PostMapping("/items")
  @ApiMessage("Thêm sản phẩm vào giỏ hàng thành công.")
  public ResponseEntity<CartRes> addItem(@Valid @RequestBody AddCartItemReq req)
      throws NotFoundException, ConflictException {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        this.cartService.addItem(req));
  }

  @PutMapping("/items/{id}")
  @ApiMessage("Cập nhật cart item thành công.")
  public ResponseEntity<CartRes> updateItem(@PathVariable("id") Long id,
                                            @Valid @RequestBody UpdateCartItemReq req)
      throws NotFoundException, ConflictException {
    return ResponseEntity.ok(this.cartService.updateItem(id, req));
  }

  @DeleteMapping("/items/{id}")
  @ApiMessage("Xóa cart item thành công.")
  public ResponseEntity<Void> deleteItem(@PathVariable("id") Long id)
      throws NotFoundException {
    this.cartService.deleteItem(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/me")
  @ApiMessage("Xóa giỏ hàng thành công.")
  public ResponseEntity<Void> deleteMyCart() throws NotFoundException {
    this.cartService.deleteMyCart();
    return ResponseEntity.noContent().build();
  }
}