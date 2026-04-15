package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.Permission;
import com.example.MiniShop.models.request.PermissionReq;
import com.example.MiniShop.models.response.PermissionDto;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

  public PermissionDto toDto(Permission permission) {
    if (permission == null) {
      return null;
    }
    PermissionDto dto = new PermissionDto();
    dto.setId(permission.getId());
    dto.setName(permission.getName());
    dto.setApiPath(permission.getApiPath());
    dto.setMethod(permission.getMethod());
    dto.setModule(permission.getModule());
    dto.setCreatedAt(permission.getCreatedAt());
    dto.setUpdatedAt(permission.getUpdatedAt());
    dto.setCreateBy(permission.getCreatedBy());
    dto.setUpdateBy(permission.getUpdatedBy());
    return dto;
  }

  public List<PermissionDto> toDtoList(List<Permission> permissions) {
    return permissions.stream().map(this::toDto).collect(Collectors.toList());
  }

  public Permission toEntity(PermissionReq req) {
    if (req == null) {
      return null;
    }
    Permission permission = new Permission();
    permission.setName(req.getName());
    permission.setApiPath(req.getApiPath());
    permission.setMethod(req.getMethod());
    permission.setModule(req.getModule());
    return permission;
  }
}
