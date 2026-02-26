package com.example.MiniShop.services.impl;

import com.example.MiniShop.mapper.UserMapper;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.request.UserReqCreate;
import com.example.MiniShop.models.response.UserResponseDto;
import com.example.MiniShop.repository.UserRepository;
import com.example.MiniShop.services.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public List<UserResponseDto> fetchAllUser() {
    List<User> users = this.userRepository.findAll();

    List<UserResponseDto> usersRep =
        users.stream().map(UserMapper::toDto).toList();

    return usersRep;
  }

  public UserResponseDto addUser(UserReqCreate userReq) {
    User user = new User();
    user.setUsername(userReq.getUsername());
    user.setPassword(userReq.getPassword());
    user.setEmail(userReq.getEmail());
    user.setPassword(this.passwordEncoder.encode(userReq.getPassword()));

    user.setActive(true);
    User savedUser = this.userRepository.save(user);

    UserResponseDto userRep = UserMapper.toDto(savedUser);

    return userRep;
  }
}
