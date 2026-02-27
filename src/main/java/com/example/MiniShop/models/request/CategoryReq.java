package com.example.MiniShop.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryReq {
  @NotBlank(message = "Tên loại không được để trống.") private String name;

  @NotBlank(message = "Mô tả loại không được để trống.")
  private String description;
}
