package com.example.MiniShop.services;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.request.UserReqCreate;
import com.example.MiniShop.models.request.UserReqUpdate;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.UserDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {
  ApiResponsePagination fetchAllUser(Specification<User> specification,
                                     Pageable pageable);
  UserDto addUser(UserReqCreate userReq)
      throws NotFoundException, ConflictException;

  UserDto fetchById(long id) throws NotFoundException;

  UserDto updateUser(long id, UserReqUpdate userReq)
      throws NotFoundException, ConflictException;


  User fetchByEmail(String email);
}
