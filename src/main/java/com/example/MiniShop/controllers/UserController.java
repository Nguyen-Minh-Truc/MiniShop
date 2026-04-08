package com.example.MiniShop.controllers;

import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.request.UserReqCreate;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.UserDto;
import com.example.MiniShop.services.UserService;
import com.example.MiniShop.services.impl.UserServiceImpl;
import com.example.MiniShop.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
  private final UserService userServiceImpl;

  @GetMapping
  @ApiMessage("Lấy tất cả người dùng. ")
  public ResponseEntity<?> getMethodName(@Filter Specification<User> spec,
                                         Pageable page) {
    ApiResponsePagination users = this.userServiceImpl.fetchAllUser(spec, page);
    return ResponseEntity.ok(users);
  }

  @PostMapping
  @ApiMessage("Thêm người dùng thành công. ")
  public ResponseEntity<?>
  postMethodName(@RequestBody @Valid UserReqCreate userReq) {
    UserDto userRep = this.userServiceImpl.addUser(userReq);
    return ResponseEntity.status(HttpStatus.CREATED).body(userRep);
  }
}
