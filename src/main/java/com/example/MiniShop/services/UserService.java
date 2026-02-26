package com.example.MiniShop.services;

import com.example.MiniShop.models.request.UserReqCreate;
import com.example.MiniShop.models.response.UserResponseDto;
import java.util.List;

public interface UserService {
  List<UserResponseDto> fetchAllUser();
  UserResponseDto addUser(UserReqCreate userReq);
}
