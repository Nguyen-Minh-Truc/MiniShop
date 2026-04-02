package com.example.MiniShop.services.impl;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.MiniShop.mapper.UserMapper;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.request.UserReqCreate;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ApiResponsePagination.Meta;
import com.example.MiniShop.models.response.UserDto;
import com.example.MiniShop.repository.UserRepository;
import com.example.MiniShop.services.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  public ApiResponsePagination fetchAllUser(Specification<User> specification,
                                    Pageable pageable) {
                            Page<User> userPagination= this.userRepository.findAll(specification, pageable);
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

  public UserDto addUser(UserReqCreate userReq) {
    User user = new User();
    user.setUsername(userReq.getUsername());
    user.setPassword(userReq.getPassword());
    user.setEmail(userReq.getEmail());
    user.setPassword(this.passwordEncoder.encode(userReq.getPassword()));

    user.setActive(true);
    User savedUser = this.userRepository.save(user);
    UserDto userRep = this.userMapper.toDto(savedUser);

    return userRep;
  }

  @Override
  public UserDto fetchById(long id) throws NotFoundException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'fetchById'");
  }
}
