package com.example.MiniShop.services.impl;

import com.example.MiniShop.exception.custom.InvalidException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.RoleMapper;
import com.example.MiniShop.models.entity.Permission;
import com.example.MiniShop.models.entity.Role;
import com.example.MiniShop.models.request.RoleReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ApiResponsePagination.Meta;
import com.example.MiniShop.models.response.RoleDto;
import com.example.MiniShop.repository.PermissionRepository;
import com.example.MiniShop.repository.RoleRepository;
import com.example.MiniShop.services.RoleService;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final RoleMapper roleMapper;

  @Override
  public ApiResponsePagination fetchAll(Specification<Role> spec,
                                        Pageable pageable) {
    Page<Role> roles = roleRepository.findAll(spec, pageable);

    Meta meta = new Meta();
    meta.setPageCurrent(pageable.getPageNumber() + 1);
    meta.setPageSize(pageable.getPageSize());
    meta.setPages(roles.getTotalPages());
    meta.setTotal(roles.getTotalElements());

    ApiResponsePagination response = new ApiResponsePagination();
    response.setMeta(meta);
    response.setResult(roleMapper.toDtoList(roles.getContent()));
    return response;
  }

  @Override
  public RoleDto create(RoleReq roleReq) throws InvalidException {
    if (this.roleRepository.existsByName(roleReq.getName())) {
      throw new InvalidException("Role already exists");
    }
    Role role = roleMapper.toEntity(roleReq);
    role.setPermissions(resolvePermissions(roleReq.getPermissionIds()));

    Role savedRole = roleRepository.save(role);
    return roleMapper.toDto(savedRole);
  }

  @Override
  public RoleDto getById(long id) throws NotFoundException {
    Role role = roleRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Role not found with id: " + id));

    return roleMapper.toDto(role);
  }

  @Override
  public RoleDto update(long id, RoleReq roleReq)
      throws NotFoundException, InvalidException {
    Role role = roleRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Role not found with id: " + id));
    if (roleRepository.existsByNameAndIdNot(roleReq.getName(), id)) {
      throw new InvalidException("Role already exists");
    }
    role.setName(roleReq.getName());
    role.setDescription(roleReq.getDescription());
    role.setPermissions(resolvePermissions(roleReq.getPermissionIds()));
    Role savedRole = roleRepository.save(role);
    return roleMapper.toDto(savedRole);
  }

  @Override
  public void deleteById(long id) throws NotFoundException {
    Role role = roleRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Role not found with id: " + id));
    roleRepository.deleteById(id);
  }

  private List<Permission> resolvePermissions(List<Long> permissionIds) {
    if (permissionIds == null || permissionIds.isEmpty()) {
      return new ArrayList<>();
    }
    Set<Long> uniqueIds = new LinkedHashSet<>(permissionIds);
    List<Permission> permissions = permissionRepository.findAllById(uniqueIds);
    return permissions;
  }
}
