package com.example.MiniShop.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.response.ProductRepDto;
import com.example.MiniShop.models.response.UserDto;

@Component
public class UserMapper {
  public UserDto toDto(User user) {
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

   public List<UserDto> toDtoList(List<User> users) {
    return users.stream().map(this::toDto).collect(Collectors.toList());
  }
}
