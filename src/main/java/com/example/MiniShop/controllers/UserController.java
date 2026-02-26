package com.example.MiniShop.controllers;

import com.example.MiniShop.models.request.UserReqCreate;
import com.example.MiniShop.models.response.UserResponseDto;
import com.example.MiniShop.services.impl.UserServiceImpl;
import com.example.MiniShop.util.annotation.ApiMessage;
import java.net.http.HttpResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
  private final UserServiceImpl userServiceImpl;

  @GetMapping
  @ApiMessage("Lấy tất cả người dùng. ")
  public ResponseEntity<?> getMethodName() {
    List<UserResponseDto> users = this.userServiceImpl.fetchAllUser();
    return ResponseEntity.ok(users);
  }

  @PostMapping
  public ResponseEntity<?> postMethodName(@RequestBody UserReqCreate userReq) {
    UserResponseDto userRep = this.userServiceImpl.addUser(userReq);
    return ResponseEntity.status(HttpStatus.CREATED).body(userRep);
  }
}
