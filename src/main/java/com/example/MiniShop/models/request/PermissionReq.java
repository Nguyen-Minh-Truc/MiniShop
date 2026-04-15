package com.example.MiniShop.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionReq {

  @NotBlank(message = "Permission name is required") private String name;

  @NotBlank(message = "API path is required") private String apiPath;

  @NotBlank(message = "Method is required") private String method;

  @NotBlank(message = "Module is required") private String module;
}
