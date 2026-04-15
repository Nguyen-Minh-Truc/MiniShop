package com.example.MiniShop.models.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionDto {
  private Long id;
  private String name;
  private String apiPath;
  private String method;
  private String module;
  private LocalDateTime createdAt;
  private String createBy;
  private String updateBy;
  private LocalDateTime updatedAt;
}
