package com.example.MiniShop.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.UserMapper;
import com.example.MiniShop.models.entity.Role;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.request.UserReqCreate;
import com.example.MiniShop.models.request.UserReqRegister;
import com.example.MiniShop.models.request.UserReqUpdate;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.UserDto;
import com.example.MiniShop.repository.ProductRepository;
import com.example.MiniShop.repository.RoleRepository;
import com.example.MiniShop.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private UserMapper userMapper;

  @Mock private RoleRepository roleRepository;

  @Mock private ProductRepository productRepository;

  @InjectMocks private UserServiceImpl userService;

  private UserReqCreate createReq;
  private UserReqUpdate updateReq;
  private UserReqRegister registerReq;
  private User user;
  private UserDto userDto;
  private Role role;

  @BeforeEach
  void setUp() {
    role = new Role();
    role.setId(1L);
    role.setName("ADMIN");

    createReq = new UserReqCreate();
    createReq.setUsername("alice");
    createReq.setEmail("alice@example.com");
    createReq.setPassword("raw-password");
    createReq.setAddress("HCM City");
    createReq.setPhone("0900000000");
    createReq.setActive(null);
    createReq.setRoleId(1L);

    updateReq = new UserReqUpdate();
    updateReq.setUsername("alice-updated");
    updateReq.setEmail("alice.updated@example.com");
    updateReq.setPassword("new-password");
    updateReq.setAddress("Da Nang");
    updateReq.setPhone("0911111111");
    updateReq.setActive(true);
    updateReq.setRoleId(1L);

    registerReq = new UserReqRegister();
    registerReq.setFullname("Alice Register");
    registerReq.setEmail("register@example.com");
    registerReq.setPassword("register-password");
    registerReq.setConfirmPassword("register-password");

    user = new User();
    user.setId(10L);
    user.setUsername("alice");
    user.setEmail("alice@example.com");
    user.setPassword("encoded-password");
    user.setAddress("HCM City");
    user.setPhone("0900000000");
    user.setActive(true);
    user.setRole(role);

    userDto = new UserDto();
    userDto.setId(10L);
    userDto.setUsername("alice");
    userDto.setEmail("alice@example.com");
    userDto.setAddress("HCM City");
    userDto.setPhone("0900000000");
    userDto.setActive(true);
  }

  @Nested
  class FetchAllUserTests {

    @Test
    void fetchAllUser_WhenUsersExist_ReturnsPaginationMetadataAndDtos() {
      // Happy path: trả về meta + danh sách DTO đúng theo Page.
      Specification<User> specification = (root, query, cb) -> null;
      Pageable pageable = PageRequest.of(1, 2);
      List<User> content = List.of(user);
      Page<User> page = new PageImpl<>(content, pageable, 5);

      when(userRepository.findAll(specification, pageable)).thenReturn(page);
      when(userMapper.toDtoList(content)).thenReturn(List.of(userDto));

      ApiResponsePagination result =
          userService.fetchAllUser(specification, pageable);

      assertThat(result).isNotNull();
      assertThat(result.getMeta().getPageCurrent()).isEqualTo(2);
      assertThat(result.getMeta().getPageSize()).isEqualTo(2);
      assertThat(result.getMeta().getPages()).isEqualTo(3);
      assertThat(result.getMeta().getTotal()).isEqualTo(5);
      assertThat(result.getResult()).isEqualTo(List.of(userDto));
      verify(userRepository, times(1)).findAll(specification, pageable);
      verify(userMapper, times(1)).toDtoList(content);
    }

    @Test
    void fetchAllUser_WhenPageIsEmpty_ReturnsEmptyResultList() {
      // Edge case: page rỗng vẫn phải trả về meta hợp lệ.
      Specification<User> specification = (root, query, cb) -> null;
      Pageable pageable = PageRequest.of(0, 10);
      List<User> content = List.of();
      Page<User> page = new PageImpl<>(content, pageable, 0);

      when(userRepository.findAll(specification, pageable)).thenReturn(page);
      when(userMapper.toDtoList(content)).thenReturn(List.of());

      ApiResponsePagination result =
          userService.fetchAllUser(specification, pageable);

      assertThat(result.getMeta().getPageCurrent()).isEqualTo(1);
      assertThat(result.getMeta().getPageSize()).isEqualTo(10);
      assertThat(result.getMeta().getPages()).isEqualTo(0);
      assertThat(result.getMeta().getTotal()).isEqualTo(0);
      assertThat(result.getResult()).isEqualTo(List.of());
    }
  }

  @Nested
  class AddUserTests {

    @Test
    void addUser_WhenEmailAlreadyExists_ThrowsConflictException() {
      // Exception case: email đã tồn tại.
      when(userRepository.existsByEmail(createReq.getEmail())).thenReturn(true);

      ConflictException exception = assertThrows(
          ConflictException.class, () -> userService.addUser(createReq));

      assertThat(exception.getMessage()).isEqualTo("Email already exists");
      verify(userRepository, times(1)).existsByEmail(createReq.getEmail());
      verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addUser_WhenRoleIdNotFound_ThrowsNotFoundException() {
      // Exception case: roleId có truyền lên nhưng không tìm thấy role.
      when(userRepository.existsByEmail(createReq.getEmail()))
          .thenReturn(false);
      when(userMapper.toEntity(createReq)).thenReturn(new User());
      when(passwordEncoder.encode(createReq.getPassword()))
          .thenReturn("encoded");
      when(roleRepository.findById(createReq.getRoleId()))
          .thenReturn(Optional.empty());

      NotFoundException exception = assertThrows(
          NotFoundException.class, () -> userService.addUser(createReq));

      assertThat(exception.getMessage()).contains("Role not found with id: 1");
      verify(roleRepository, times(1)).findById(1L);
      verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void
    addUser_WhenRequestIsValid_SavesUserWithEncodedPasswordAndDefaultActive()
        throws Exception {
      // Happy path + edge: active null thì mặc định true, password phải được
      // encode.
      User mappedUser = new User();
      User savedUser = new User();
      savedUser.setId(10L);

      when(userRepository.existsByEmail(createReq.getEmail()))
          .thenReturn(false);
      when(userMapper.toEntity(createReq)).thenReturn(mappedUser);
      when(passwordEncoder.encode(createReq.getPassword()))
          .thenReturn("encoded-password");
      when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
      when(userRepository.save(mappedUser)).thenReturn(savedUser);
      when(userMapper.toDto(savedUser)).thenReturn(userDto);

      UserDto result = userService.addUser(createReq);

      assertThat(result).isEqualTo(userDto);

      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository, times(1)).save(userCaptor.capture());
      User captured = userCaptor.getValue();
      assertThat(captured.getPassword()).isEqualTo("encoded-password");
      assertThat(captured.getAddress()).isEqualTo(createReq.getAddress());
      assertThat(captured.getPhone()).isEqualTo(createReq.getPhone());
      assertThat(captured.isActive()).isTrue();
      assertThat(captured.getRole()).isEqualTo(role);
    }
  }

  @Nested
  class RegisterTests {

    @Test
    void register_WhenPasswordsMismatch_ThrowsConflictException() {
      // Exception case: password và confirm password không trùng nhau.
      registerReq.setConfirmPassword("different-password");

      ConflictException exception = assertThrows(
          ConflictException.class, () -> userService.register(registerReq));

      assertThat(exception.getMessage())
          .isEqualTo("Nhập lại mật khẩu không đúng. ");
      verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WhenRequestIsValid_SavesUserAndReturnsDto() throws Exception {
      // Happy path: đăng ký thành công và set active=true.
      when(passwordEncoder.encode(registerReq.getPassword()))
          .thenReturn("encoded-register-password");
      when(userRepository.save(any(User.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));
      when(userMapper.toDto(any(User.class))).thenReturn(userDto);

      UserDto result = userService.register(registerReq);

      assertThat(result).isEqualTo(userDto);

      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository, times(1)).save(userCaptor.capture());
      User captured = userCaptor.getValue();
      assertThat(captured.getUsername()).isEqualTo(registerReq.getFullname());
      assertThat(captured.getEmail()).isEqualTo(registerReq.getEmail());
      assertThat(captured.getPassword()).isEqualTo("encoded-register-password");
      assertThat(captured.isActive()).isTrue();
    }
  }

  @Nested
  class FetchByIdTests {

    @Test
    void fetchById_WhenUserExists_ReturnsUserDto() throws Exception {
      // Happy path: tìm thấy user theo id.
      when(userRepository.findById(10L)).thenReturn(Optional.of(user));
      when(userMapper.toDto(user)).thenReturn(userDto);

      UserDto result = userService.fetchById(10L);

      assertThat(result).isEqualTo(userDto);
      verify(userRepository, times(1)).findById(10L);
      verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void fetchById_WhenUserNotFound_ThrowsNotFoundException() {
      // Exception case: không có user theo id.
      when(userRepository.findById(999L)).thenReturn(Optional.empty());

      NotFoundException exception = assertThrows(
          NotFoundException.class, () -> userService.fetchById(999L));

      assertThat(exception.getMessage())
          .isEqualTo("User not found with id: 999");
    }
  }

  @Nested
  class UpdateUserTests {

    @Test
    void updateUser_WhenUserNotFound_ThrowsNotFoundException() {
      // Exception case: update id không tồn tại.
      when(userRepository.findById(999L)).thenReturn(Optional.empty());

      NotFoundException exception =
          assertThrows(NotFoundException.class,
                       () -> userService.updateUser(999L, updateReq));

      assertThat(exception.getMessage())
          .isEqualTo("User not found with id: 999");
    }

    @Test
    void updateUser_WhenEmailChangedAndEmailExists_ThrowsConflictException() {
      // Exception case: đổi sang email đã tồn tại ở user khác.
      user.setEmail("old@example.com");
      updateReq.setEmail("new@example.com");

      when(userRepository.findById(10L)).thenReturn(Optional.of(user));
      when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

      ConflictException exception =
          assertThrows(ConflictException.class,
                       () -> userService.updateUser(10L, updateReq));

      assertThat(exception.getMessage()).isEqualTo("Email already exists");
      verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void
    updateUser_WhenPasswordBlankAndActiveNull_UpdatesWithoutEncodingOrActiveOverride()
        throws Exception {
      // Edge case: password blank thì không encode, active null thì giữ nguyên
      // giá trị cũ.
      user.setActive(false);
      updateReq.setPassword("   ");
      updateReq.setActive(null);
      updateReq.setRoleId(null);
      updateReq.setEmail(user.getEmail());

      when(userRepository.findById(10L)).thenReturn(Optional.of(user));
      when(userRepository.save(user)).thenReturn(user);
      when(userMapper.toDto(user)).thenReturn(userDto);

      UserDto result = userService.updateUser(10L, updateReq);

      assertThat(result).isEqualTo(userDto);
      assertThat(user.isActive()).isFalse();
      verify(passwordEncoder, never()).encode(any(String.class));
      verify(roleRepository, never()).findById(any(Long.class));
    }

    @Test
    void updateUser_WhenRoleIdNotFound_ThrowsNotFoundException() {
      // Exception case: roleId truyền lên nhưng role không tồn tại.
      updateReq.setEmail(user.getEmail());
      when(userRepository.findById(10L)).thenReturn(Optional.of(user));
      when(roleRepository.findById(1L)).thenReturn(Optional.empty());

      NotFoundException exception =
          assertThrows(NotFoundException.class,
                       () -> userService.updateUser(10L, updateReq));

      assertThat(exception.getMessage()).contains("Role not found with id: 1");
      verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WhenRequestIsValid_UpdatesAndReturnsDto() throws Exception {
      // Happy path: update đầy đủ trường, encode password, gán role mới.
      when(userRepository.findById(10L)).thenReturn(Optional.of(user));
      when(userRepository.existsByEmail(updateReq.getEmail()))
          .thenReturn(false);
      when(passwordEncoder.encode(updateReq.getPassword()))
          .thenReturn("encoded-new-password");
      when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
      when(userRepository.save(user)).thenReturn(user);
      when(userMapper.toDto(user)).thenReturn(userDto);

      UserDto result = userService.updateUser(10L, updateReq);

      assertThat(result).isEqualTo(userDto);
      assertThat(user.getUsername()).isEqualTo(updateReq.getUsername());
      assertThat(user.getEmail()).isEqualTo(updateReq.getEmail());
      assertThat(user.getAddress()).isEqualTo(updateReq.getAddress());
      assertThat(user.getPhone()).isEqualTo(updateReq.getPhone());
      assertThat(user.isActive()).isEqualTo(updateReq.getActive());
      assertThat(user.getPassword()).isEqualTo("encoded-new-password");
      assertThat(user.getRole()).isEqualTo(role);
      verify(userRepository, times(1)).save(user);
    }
  }

  @Nested
  class FetchByEmailTests {

    @Test
    void fetchByEmail_WhenEmailProvided_ReturnsUserFromRepository() {
      // Happy path: fetchByEmail chỉ forward xuống repository.
      when(userRepository.findByEmail("alice@example.com")).thenReturn(user);

      User result = userService.fetchByEmail("alice@example.com");

      assertThat(result).isEqualTo(user);
      verify(userRepository, times(1)).findByEmail("alice@example.com");
    }
  }

  @Nested
  class GetUserByRefreshTokenAnhEmailTests {

    @Test
    void
    getUserByRefreshTokenAnhEmail_WhenTokenAndEmailProvided_ReturnsMatchedUser() {
      // Happy path: trả về user theo refresh token + email.
      when(userRepository.findByRefreshTokenAndEmail("token-1",
                                                     "alice@example.com"))
          .thenReturn(user);

      User result = userService.getUserByRefreshTokenAnhEmail(
          "token-1", "alice@example.com");

      assertThat(result).isEqualTo(user);
      verify(userRepository, times(1))
          .findByRefreshTokenAndEmail("token-1", "alice@example.com");
    }
  }

  @Nested
  class UpdateRefreshTokenTests {

    @Test
    void updateRefreshToken_WhenUserExists_UpdatesTokenAndSavesUser() {
      // Happy path: có user thì cập nhật refresh token và save.
      when(userRepository.findByEmail("alice@example.com")).thenReturn(user);

      userService.UpdateRefreshToken("new-token", "alice@example.com");

      assertThat(user.getRefreshToken()).isEqualTo("new-token");
      verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateRefreshToken_WhenUserNotFound_DoesNothing() {
      // Edge case: không có user thì không save.
      when(userRepository.findByEmail("missing@example.com")).thenReturn(null);

      userService.UpdateRefreshToken("new-token", "missing@example.com");

      verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void
    updateRefreshToken_WhenRepositorySaveThrowsException_PropagatesException() {
      // Exception case: minh họa doThrow cho tình huống save ném lỗi.
      RuntimeException runtimeException = new RuntimeException("db-error");
      doThrow(runtimeException).when(userRepository).save(any(User.class));
      when(userRepository.findByEmail("alice@example.com")).thenReturn(user);

      RuntimeException thrown =
          assertThrows(RuntimeException.class,
                       ()
                           -> userService.UpdateRefreshToken(
                               "new-token", "alice@example.com"));

      assertThat(thrown.getMessage()).isEqualTo("db-error");
    }
  }
}
