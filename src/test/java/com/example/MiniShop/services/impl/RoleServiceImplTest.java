package com.example.MiniShop.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.MiniShop.exception.custom.InvalidException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.RoleMapper;
import com.example.MiniShop.models.entity.Role;
import com.example.MiniShop.models.request.RoleReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.RoleDto;
import com.example.MiniShop.repository.PermissionRepository;
import com.example.MiniShop.repository.RoleRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

  @Mock private RoleRepository roleRepository;
  @Mock private PermissionRepository permissionRepository;
  @Mock private RoleMapper roleMapper;

  @InjectMocks private RoleServiceImpl roleService;

  private RoleReq req;
  private Role role;
  private RoleDto dto;

  @BeforeEach
  void setUp() {
    req = new RoleReq();
    req.setName("ADMIN");
    req.setDescription("Administrator");
    req.setPermissionIds(List.of(1L, 2L));

    role = new Role();
    role.setId(1L);
    role.setName("ADMIN");

    dto = new RoleDto();
    dto.setId(1L);
    dto.setName("ADMIN");
  }

  @Nested
  class FetchAllTests {
    @Test
    void fetchAll_WhenPageHasData_ReturnsPaginationResponse() {
      // Happy path: trả về role theo page.
      Specification<Role> spec = (root, query, cb) -> null;
      Pageable pageable = PageRequest.of(0, 10);
      Page<Role> page = new PageImpl<>(List.of(role), pageable, 1);

      when(roleRepository.findAll(spec, pageable)).thenReturn(page);
      when(roleMapper.toDtoList(List.of(role))).thenReturn(List.of(dto));

      ApiResponsePagination result = roleService.fetchAll(spec, pageable);

      assertThat(result.getMeta().getPageCurrent()).isEqualTo(1);
      assertThat(result.getResult()).isEqualTo(List.of(dto));
    }
  }

  @Nested
  class CreateTests {
    @Test
    void create_WhenRoleAlreadyExists_ThrowsInvalidException() {
      // Exception case: role name đã tồn tại.
      when(roleRepository.existsByName(req.getName())).thenReturn(true);

      InvalidException thrown =
          assertThrows(InvalidException.class, () -> roleService.create(req));

      assertThat(thrown.getMessage()).isEqualTo("Role already exists");
    }
  }

  @Nested
  class GetByIdTests {
    @Test
    void getById_WhenRoleNotFound_ThrowsNotFoundException() {
      // Exception case: không tìm thấy role.
      when(roleRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException thrown =
          assertThrows(NotFoundException.class, () -> roleService.getById(99L));

      assertThat(thrown.getMessage()).isEqualTo("Role not found with id: 99");
    }
  }

  @Nested
  class UpdateTests {
    @Test
    void
    update_WhenRoleNameAlreadyExistsOnAnotherRole_ThrowsInvalidException() {
      // Exception case: tên role mới bị trùng role khác.
      when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
      when(roleRepository.existsByNameAndIdNot(req.getName(), 1L))
          .thenReturn(true);

      InvalidException thrown = assertThrows(InvalidException.class,
                                             () -> roleService.update(1L, req));

      assertThat(thrown.getMessage()).isEqualTo("Role already exists");
    }
  }

  @Nested
  class DeleteByIdTests {
    @Test
    void deleteById_WhenRoleNotFound_ThrowsNotFoundException() {
      // Exception case: role cần xóa không tồn tại.
      when(roleRepository.findById(77L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> roleService.deleteById(77L));

      assertThat(thrown.getMessage()).isEqualTo("Role not found with id: 77");
    }
  }
}
