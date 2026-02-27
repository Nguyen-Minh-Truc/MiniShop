package com.example.MiniShop.models.response;

import java.util.List;

import com.example.MiniShop.models.entity.Product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDetailDto {
   private String name;

 
  private String description;

  private boolean active = true;

  private List<ProductRepDto> products;
}
