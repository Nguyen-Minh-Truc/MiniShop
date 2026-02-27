package com.example.MiniShop.models.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRepDto {
  
  private String name;

 
  private String description;

  private boolean active = true;
}
