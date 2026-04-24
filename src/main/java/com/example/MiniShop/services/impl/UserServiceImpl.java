package com.example.MiniShop.services.impl;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.UserMapper;
import com.example.MiniShop.models.entity.Role;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.request.UserReqCreate;
import com.example.MiniShop.models.request.UserReqRegister;
import com.example.MiniShop.models.request.UserReqUpdate;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ApiResponsePagination.Meta;
import com.example.MiniShop.models.response.UserDto;
import com.example.MiniShop.repository.RoleRepository;
import com.example.MiniShop.repository.UserRepository;
import com.example.MiniShop.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final RoleRepository roleRepository;
  private final DashboardRealtimeNotifier dashboardRealtimeNotifier;

  public ApiResponsePagination fetchAllUser(Specification<User> specification,
                                            Pageable pageable) {
    Page<User> userPagination =
        this.userRepository.findAll(specification, pageable);
    Meta meta = new Meta();
    meta.setPageCurrent(pageable.getPageNumber() + 1);
    meta.setPageSize(pageable.getPageSize());

    meta.setPages(userPagination.getTotalPages());
    meta.setTotal(userPagination.getTotalElements());
    ApiResponsePagination result = new ApiResponsePagination();

    result.setMeta(meta);
    result.setResult(this.userMapper.toDtoList(userPagination.getContent()));
    return result;
  }

  @Override
  public UserDto addUser(UserReqCreate userReq)
      throws NotFoundException, ConflictException {
    if (userRepository.existsByEmail(userReq.getEmail())) {
      throw new ConflictException("Email already exists");
    }

    User user = userMapper.toEntity(userReq);
    user.setPassword(passwordEncoder.encode(userReq.getPassword()));
    user.setAddress(userReq.getAddress());
    user.setPhone(userReq.getPhone());
    user.setActive(userReq.getActive() == null || userReq.getActive());
    Long roleId = userReq.getRoleId();
    if (roleId != null) {
      Role role = roleRepository.findById(roleId).orElseThrow(
          () -> new NotFoundException("Role not found with id: " + roleId));
      user.setRole(role);
    }

    User savedUser = this.userRepository.save(user);
    dashboardRealtimeNotifier.publishAll("USER_CREATED");
    return this.userMapper.toDto(savedUser);
  }

  public UserDto register(UserReqRegister userReq) throws ConflictException {
    if (!userReq.getPassword().equals(userReq.getConfirmPassword())) {
      throw new ConflictException("Nhập lại mật khẩu không đúng. ");
    }
    User user = new User();
    user.setUsername(userReq.getFullname());
    user.setPassword(userReq.getPassword());
    user.setEmail(userReq.getEmail());
    user.setPassword(this.passwordEncoder.encode(userReq.getPassword()));
    user.setActive(true);
    User savedUser = this.userRepository.save(user);
    dashboardRealtimeNotifier.publishAll("USER_REGISTERED");
    UserDto userRep = this.userMapper.toDto(savedUser);
    return userRep;
  }

  @Override
  public UserDto fetchById(long id) throws NotFoundException {
    User user = userRepository.findById(id).orElseThrow(
        () -> new NotFoundException("User not found with id: " + id));
    return userMapper.toDto(user);
  }

  @Override
  @Transactional
  public UserDto updateUser(long id, UserReqUpdate userReq)
      throws NotFoundException, ConflictException {
    User user = userRepository.findById(id).orElseThrow(
        () -> new NotFoundException("User not found with id: " + id));

    if (!user.getEmail().equals(userReq.getEmail()) &&
        userRepository.existsByEmail(userReq.getEmail())) {
      throw new ConflictException("Email already exists");
    }

    user.setUsername(userReq.getUsername());
    user.setEmail(userReq.getEmail());
    user.setAddress(userReq.getAddress());
    user.setPhone(userReq.getPhone());
    if (userReq.getActive() != null) {
      user.setActive(userReq.getActive());
    }
    if (userReq.getPassword() != null && !userReq.getPassword().isBlank()) {
      user.setPassword(passwordEncoder.encode(userReq.getPassword()));
    }
    Long roleId = userReq.getRoleId();
    if (roleId != null) {
      Role role = roleRepository.findById(roleId).orElseThrow(
          () -> new NotFoundException("Role not found with id: " + roleId));
      user.setRole(role);
    }

    User savedUser = userRepository.save(user);
    dashboardRealtimeNotifier.publishAll("USER_UPDATED");
    return userMapper.toDto(savedUser);
  }

  @Override
  public User fetchByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public User getUserByRefreshTokenAnhEmail(String token, String email) {
    return this.userRepository.findByRefreshTokenAndEmail(token, email);
  }

  public void UpdateRefreshToken(String token, String email) {
    User currentUser = this.fetchByEmail(email);
    if (currentUser != null) {
      currentUser.setRefreshToken(token);
      this.userRepository.save(currentUser);
    }
  }
}
