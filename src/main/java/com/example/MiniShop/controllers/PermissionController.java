package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.InvalidException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Permission;
import com.example.MiniShop.models.request.PermissionReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.PermissionDto;
import com.example.MiniShop.services.PermissionService;
import com.example.MiniShop.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {
  private final PermissionService permissionService;

  @GetMapping
  @ApiMessage("Lấy danh sách quyền thành công.")
  public ResponseEntity<ApiResponsePagination>
  getAllPermissions(@Filter Specification<Permission> spec, Pageable pageable) {
    return ResponseEntity.ok(permissionService.fetchAll(spec, pageable));
  }

  @PostMapping
  @ApiMessage("Tạo quyền thành công.")
  public ResponseEntity<PermissionDto>
  createPermission(@Valid @RequestBody PermissionReq permissionReq)
      throws InvalidException {
    PermissionDto dto = permissionService.create(permissionReq);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @GetMapping("/{id}")
  @ApiMessage("Lấy quyền theo id thành công.")
  public ResponseEntity<PermissionDto>
  getPermissionById(@PathVariable("id") long id) throws NotFoundException {
    PermissionDto dto = permissionService.getById(id);
    return ResponseEntity.ok(dto);
  }

  @PutMapping("/{id}")
  @ApiMessage("Cập nhật quyền thành công.")
  public ResponseEntity<PermissionDto>
  updatePermission(@PathVariable("id") long id,
                   @Valid @RequestBody PermissionReq permissionReq)
      throws NotFoundException, InvalidException {
    PermissionDto dto = permissionService.update(id, permissionReq);
    return ResponseEntity.ok(dto);
  }
}
