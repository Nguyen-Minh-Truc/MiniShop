package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.response.UserDto;

public class UserMapper {
  public static UserDto toDto(User user) {
    if (user == null)
      return null;
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setAddress(user.getAddress());
    dto.setPhone(user.getPhone());
    dto.setActive(user.isActive());
    dto.setCreatedAt(user.getCreatedAt());
    return dto;
  }
}
