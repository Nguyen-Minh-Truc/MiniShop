package com.example.MiniShop.services;

import com.example.MiniShop.exception.custom.InvalidException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Permission;
import com.example.MiniShop.models.request.PermissionReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.PermissionDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PermissionService {
  ApiResponsePagination fetchAll(Specification<Permission> spec,
                                 Pageable pageable);

  PermissionDto create(PermissionReq permissionReq) throws InvalidException;

  PermissionDto getById(long id) throws NotFoundException;

  PermissionDto update(long id, PermissionReq permissionReq)
      throws NotFoundException, InvalidException;
}
