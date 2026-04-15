package com.example.MiniShop.services;

import com.example.MiniShop.exception.custom.InvalidException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Role;
import com.example.MiniShop.models.request.RoleReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.RoleDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface RoleService {
  ApiResponsePagination fetchAll(Specification<Role> spec, Pageable pageable);

  RoleDto create(RoleReq roleReq) throws InvalidException;

  RoleDto getById(long id) throws NotFoundException;

  RoleDto update(long id, RoleReq roleReq) throws NotFoundException, InvalidException;
}
