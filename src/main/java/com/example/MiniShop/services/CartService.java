package com.example.MiniShop.services;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.request.AddCartItemReq;
import com.example.MiniShop.models.request.UpdateCartItemReq;
import com.example.MiniShop.models.response.CartRes;

public interface CartService {
  CartRes getMyCart() throws NotFoundException;

  CartRes addItem(AddCartItemReq req) throws NotFoundException, ConflictException;

  CartRes updateItem(Long itemId, UpdateCartItemReq req)
      throws NotFoundException, ConflictException;

  void deleteItem(Long itemId) throws NotFoundException;

  void deleteMyCart() throws NotFoundException;
}