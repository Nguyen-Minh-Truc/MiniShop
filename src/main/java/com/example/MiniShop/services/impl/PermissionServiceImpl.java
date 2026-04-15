package com.example.MiniShop.services.impl;

import com.example.MiniShop.exception.custom.InvalidException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.PermissionMapper;
import com.example.MiniShop.models.entity.Permission;
import com.example.MiniShop.models.entity.Role;
import com.example.MiniShop.models.request.PermissionReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ApiResponsePagination.Meta;
import com.example.MiniShop.models.response.PermissionDto;
import com.example.MiniShop.repository.PermissionRepository;
import com.example.MiniShop.repository.RoleRepository;
import com.example.MiniShop.services.PermissionService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
  private final PermissionRepository permissionRepository;
  private final RoleRepository roleRepository;
  private final PermissionMapper permissionMapper;

  @Override
  public ApiResponsePagination fetchAll(Specification<Permission> spec,
                                        Pageable pageable) {
    Pageable safePageable = Objects.requireNonNull(pageable);
    Page<Permission> permissions =
        permissionRepository.findAll(spec, safePageable);

    Meta meta = new Meta();
    meta.setPageCurrent(safePageable.getPageNumber() + 1);
    meta.setPageSize(safePageable.getPageSize());
    meta.setPages(permissions.getTotalPages());
    meta.setTotal(permissions.getTotalElements());

    ApiResponsePagination response = new ApiResponsePagination();
    response.setMeta(meta);
    response.setResult(permissionMapper.toDtoList(permissions.getContent()));

    return response;
  }

  @Override
  public PermissionDto create(PermissionReq permissionReq)
      throws InvalidException {
    boolean exists = permissionRepository.existsByModuleAndApiPathAndMethod(
        permissionReq.getModule(), permissionReq.getApiPath(),
        permissionReq.getMethod());
    if (exists) {
      throw new InvalidException("Permission already exists");
    }
    Permission permission = permissionMapper.toEntity(permissionReq);
    Permission savedPermission = permissionRepository.save(permission);
    return permissionMapper.toDto(savedPermission);
  }

  @Override
  public PermissionDto getById(long id) throws NotFoundException {
    Permission permission = permissionRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Permission not found with id: " + id));
    return permissionMapper.toDto(permission);
  }

  @Override
  public PermissionDto update(long id, PermissionReq permissionReq)
      throws NotFoundException, InvalidException {
    Permission permission = permissionRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Permission not found with id: " + id));
    boolean exists = permissionRepository.existsByModuleAndApiPathAndMethod(
        permissionReq.getModule(), permissionReq.getApiPath(),
        permissionReq.getMethod());
    if (exists) {
      throw new InvalidException("Permission already exists");
    }
    permission.setName(permissionReq.getName());
    permission.setApiPath(permissionReq.getApiPath());
    permission.setMethod(permissionReq.getMethod());
    permission.setModule(permissionReq.getModule());
    Permission savedPermission = permissionRepository.save(permission);
    return permissionMapper.toDto(savedPermission);
  }
  @Transactional
  @Override
  public void deleteById(long id) throws NotFoundException {

    Permission permission = permissionRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Permission not found with id: " + id));

    permission.getRoles().forEach(
        role -> role.getPermissions().remove(permission));

    permissionRepository.delete(permission);
  }
}
