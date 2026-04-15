package com.example.MiniShop.models.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleReq {

  @NotBlank(message = "Role name is required") private String name;

   @NotBlank(message = "Role description is required") 
  private String description;

  private List<Long> permissionIds;
}
