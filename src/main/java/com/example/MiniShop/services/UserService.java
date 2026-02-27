package com.example.MiniShop.services;

import com.example.MiniShop.models.request.UserReqCreate;
import com.example.MiniShop.models.response.UserDto;
import java.util.List;

public interface UserService {
  List<UserDto> fetchAllUser();
  UserDto addUser(UserReqCreate userReq);
}
