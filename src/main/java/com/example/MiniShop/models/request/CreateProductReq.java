package com.example.MiniShop.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductReq {

  @NotBlank(message = "Name is required") private String name;

  private String description;

  @NotNull(message = "Category is required") private Long categoryId;

  private List<String> imageUrls;
}