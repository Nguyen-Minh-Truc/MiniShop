package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.InvalidException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Role;
import com.example.MiniShop.models.request.RoleReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.RoleDto;
import com.example.MiniShop.services.RoleService;
import com.example.MiniShop.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
  private final RoleService roleService;

  @GetMapping
  @ApiMessage("Lấy danh sách vai trò thành công.")
  public ResponseEntity<ApiResponsePagination>
  getAllRoles(@Filter Specification<Role> spec, Pageable pageable) {
    return ResponseEntity.ok(roleService.fetchAll(spec, pageable));
  }

  @PostMapping
  @ApiMessage("Tạo vai trò thành công.")
  public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleReq roleReq)
      throws InvalidException {
    RoleDto dto = roleService.create(roleReq);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @GetMapping("/{id}")
  @ApiMessage("Lấy vai trò theo id thành công.")
  public ResponseEntity<RoleDto> getRoleById(@PathVariable("id") long id)
      throws NotFoundException {
    RoleDto dto = roleService.getById(id);
    return ResponseEntity.ok(dto);
  }

  @PutMapping("/{id}")
  @ApiMessage("Cập nhật vai trò thành công.")
  public ResponseEntity<RoleDto> updateRole(@PathVariable("id") Long id,
                                            @Valid @RequestBody RoleReq roleReq)
      throws NotFoundException, InvalidException {
    RoleDto dto = roleService.update(id, roleReq);
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping("/{id}")
  @ApiMessage("Xoá vai trò thành công.")
  public ResponseEntity<Void> deleteRole(@PathVariable("id") long id)
      throws NotFoundException {
    roleService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
