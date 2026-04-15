package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.Role;
import com.example.MiniShop.models.request.RoleReq;
import com.example.MiniShop.models.response.RoleDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleMapper {
  private final PermissionMapper permissionMapper;

  public RoleDto toDto(Role role) {
    if (role == null) {
      return null;
    }

    RoleDto dto = new RoleDto();
    dto.setId(role.getId());
    dto.setName(role.getName());
    dto.setDescription(role.getDescription());
    dto.setCreatedAt(role.getCreatedAt());
    dto.setUpdatedAt(role.getUpdatedAt());
    dto.setPermissions(permissionMapper.toDtoList(role.getPermissions()));
    return dto;
  }

  public List<RoleDto> toDtoList(List<Role> roles) {
    return roles.stream().map(this::toDto).collect(Collectors.toList());
  }

  public Role toEntity(RoleReq req) {
    if (req == null) {
      return null;
    }
    Role role = new Role();
    role.setName(req.getName());
    role.setDescription(req.getDescription());
    return role;
  }
}
