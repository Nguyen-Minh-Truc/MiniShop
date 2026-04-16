package com.example.MiniShop.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.MiniShop.exception.custom.InvalidException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.PermissionMapper;
import com.example.MiniShop.models.entity.Permission;
import com.example.MiniShop.models.request.PermissionReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.PermissionDto;
import com.example.MiniShop.repository.PermissionRepository;
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
class PermissionServiceImplTest {

  @Mock private PermissionRepository permissionRepository;
  @Mock private PermissionMapper permissionMapper;

  @InjectMocks private PermissionServiceImpl permissionService;

  private PermissionReq req;
  private Permission permission;
  private PermissionDto dto;

  @BeforeEach
  void setUp() {
    req = new PermissionReq();
    req.setName("READ_USER");
    req.setModule("USER");
    req.setApiPath("/api/users");
    req.setMethod("GET");

    permission = new Permission();
    permission.setId(1L);
    permission.setName("READ_USER");

    dto = new PermissionDto();
    dto.setId(1L);
    dto.setName("READ_USER");
  }

  @Nested
  class FetchAllTests {
    @Test
    void fetchAll_WhenPageHasData_ReturnsPaginationResponse() {
      // Happy path: trả về page permissions.
      Specification<Permission> spec = (root, query, cb) -> null;
      Pageable pageable = PageRequest.of(0, 10);
      Page<Permission> page = new PageImpl<>(List.of(permission), pageable, 1);

      when(permissionRepository.findAll(spec, pageable)).thenReturn(page);
      when(permissionMapper.toDtoList(List.of(permission)))
          .thenReturn(List.of(dto));

      ApiResponsePagination result = permissionService.fetchAll(spec, pageable);

      assertThat(result.getMeta().getPageCurrent()).isEqualTo(1);
      assertThat(result.getResult()).isEqualTo(List.of(dto));
    }
  }

  @Nested
  class CreateTests {
    @Test
    void create_WhenPermissionAlreadyExists_ThrowsInvalidException() {
      // Exception case: permission trùng module + apiPath + method.
      when(permissionRepository.existsByModuleAndApiPathAndMethod(
               req.getModule(), req.getApiPath(), req.getMethod()))
          .thenReturn(true);

      InvalidException thrown = assertThrows(
          InvalidException.class, () -> permissionService.create(req));

      assertThat(thrown.getMessage()).isEqualTo("Permission already exists");
    }
  }

  @Nested
  class GetByIdTests {
    @Test
    void getById_WhenPermissionNotFound_ThrowsNotFoundException() {
      // Exception case: không tìm thấy permission.
      when(permissionRepository.findById(99L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> permissionService.getById(99L));

      assertThat(thrown.getMessage())
          .isEqualTo("Permission not found with id: 99");
    }
  }

  @Nested
  class UpdateTests {
    @Test
    void update_WhenPermissionNotFound_ThrowsNotFoundException() {
      // Exception case: id update không tồn tại.
      when(permissionRepository.findById(100L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> permissionService.update(100L, req));

      assertThat(thrown.getMessage())
          .isEqualTo("Permission not found with id: 100");
    }
  }

  @Nested
  class DeleteByIdTests {
    @Test
    void deleteById_WhenPermissionNotFound_ThrowsNotFoundException() {
      // Exception case: id xóa không tồn tại.
      when(permissionRepository.findById(77L)).thenReturn(Optional.empty());

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> permissionService.deleteById(77L));

      assertThat(thrown.getMessage())
          .isEqualTo("Permission not found with id: 77");
    }
  }
}
