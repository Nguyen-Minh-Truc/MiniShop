package com.example.MiniShop.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  @NotBlank(message = "Tên loại không được để trống.") private String name;

  @NotBlank(message = "Mô tả loại không được để trống.")
  private String description;

  private boolean active = true;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  private List<Product> products;
}