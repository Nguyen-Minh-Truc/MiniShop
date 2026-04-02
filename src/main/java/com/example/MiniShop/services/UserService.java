package com.example.MiniShop.services;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.request.UserReqCreate;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.UserDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {
  ApiResponsePagination fetchAllUser(Specification<User> specification,
                                     Pageable pageable);
  UserDto addUser(UserReqCreate userReq);

  UserDto fetchById(long id) throws NotFoundException; 
}
