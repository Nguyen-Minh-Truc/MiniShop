package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.Role;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.request.UserReqCreate;
import com.example.MiniShop.models.request.UserReqUpdate;
import com.example.MiniShop.models.response.RoleRes;
import com.example.MiniShop.models.response.UserDto;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public UserDto toDto(User user) {
    if (user == null) {
      return null;
    }
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setAddress(user.getAddress());
    dto.setPhone(user.getPhone());
    dto.setActive(user.isActive());
    dto.setCreatedAt(user.getCreatedAt());
    dto.setRole(toRoleRes(user.getRole()));
    return dto;
  }

  public List<UserDto> toDtoList(List<User> users) {
    return users.stream().map(this::toDto).collect(Collectors.toList());
  }

  public User toEntity(UserReqCreate req) {
    if (req == null) {
      return null;
    }

    User user = new User();
    user.setUsername(req.getUsername());
    user.setEmail(req.getEmail());
    user.setPassword(req.getPassword());
    user.setAddress(req.getAddress());
    user.setPhone(req.getPhone());
    if (req.getActive() != null) {
      user.setActive(req.getActive());
    }
    return user;
  }

  public User toEntity(UserReqUpdate req) {
    if (req == null) {
      return null;
    }

    User user = new User();
    user.setUsername(req.getUsername());
    user.setEmail(req.getEmail());
    user.setPassword(req.getPassword());
    user.setAddress(req.getAddress());
    user.setPhone(req.getPhone());
    if (req.getActive() != null) {
      user.setActive(req.getActive());
    }
    return user;
  }

  private RoleRes toRoleRes(Role role) {
    if (role == null) {
      return null;
    }

    RoleRes roleRes = new RoleRes();
    roleRes.setName(role.getName());
    roleRes.setDescription(role.getDescription());
    return roleRes;
  }
}
