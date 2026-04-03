package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.models.request.LoginReq;
import com.example.MiniShop.models.request.UserReqCreate;
import com.example.MiniShop.models.request.UserReqRegister;
import com.example.MiniShop.models.response.LoginRes;
import com.example.MiniShop.models.response.LoginRes.UserLogin;
import com.example.MiniShop.models.response.UserDto;
import com.example.MiniShop.services.impl.UserServiceImpl;
import com.example.MiniShop.util.SecurityUtil;
import com.example.MiniShop.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final SecurityUtil securityUtil;
  private final UserServiceImpl userServiceImpl;

  @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
  private long refreshTokenExpiration;
  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginReq loginReq) {

    // Nạp input gồm username/password vào Security
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(loginReq.getUsername(),
                                                loginReq.getPassword());

    // xác thực người dùng => cần viết hàm loadUserByUsername
    Authentication authentication =
        this.authenticationManagerBuilder.getObject().authenticate(
            authenticationToken);

    //  lưu thông tin người dùng đăng nhập vào Security Context Holder
    SecurityContextHolder.getContext().setAuthentication(authentication);

    User currentUser =
        this.userServiceImpl.fetchByEmail(loginReq.getUsername());
    UserLogin userLogin = new UserLogin(
        currentUser.getId(), currentUser.getUsername(), currentUser.getEmail());

    //  Tạo access Token
    String access_token =
        this.securityUtil.createAccessToken(loginReq.getUsername(), userLogin);

    LoginRes loginRes = new LoginRes();
    loginRes.setAccessToken(access_token);
    loginRes.setUserLogin(userLogin);

    // create refresh Token
    String refreshToken =
        this.securityUtil.createRefreshToken(loginReq.getUsername(), loginRes);

    // update user token
    this.userServiceImpl.UpdateRefreshToken(refreshToken,
                                            loginReq.getUsername());

    ResponseCookie responseCookie =
        ResponseCookie.from("refresh_Token", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(refreshTokenExpiration)
            .build();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
        .body(loginRes);
  }

  //   dùng khi người dùng f5 là trang
  @GetMapping("/account")
  @ApiMessage("fetch Account")
  public ResponseEntity<?> getAccount() {
    String email = this.securityUtil.getCurrentUserLogin().isPresent()
                       ? this.securityUtil.getCurrentUserLogin().get()
                       : "";

    User currentUser = this.userServiceImpl.fetchByEmail(email);
    UserLogin userLogin = new UserLogin(
        currentUser.getId(), currentUser.getEmail(), currentUser.getUsername());
    return ResponseEntity.ok().body(userLogin);
  }

  @GetMapping("/refresh")
  @ApiMessage("get user by refresh token")
  public ResponseEntity<?>
  getRefreshToken(@CookieValue(name = "refresh_Token") String refresh_token) {
    Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);

    String email = decodedToken.getSubject();

    User user = this.userServiceImpl.getUserByRefreshTokenAnhEmail(
        refresh_token, email);

    LoginRes resLoginDTO = new LoginRes();
    User currentUser = this.userServiceImpl.fetchByEmail(email);
    UserLogin userLogin = new UserLogin(
        currentUser.getId(), currentUser.getEmail(), currentUser.getUsername());

    String access_token = this.securityUtil.createAccessToken(email, userLogin);
    resLoginDTO.setAccessToken(access_token);

    resLoginDTO.setUserLogin(userLogin);

    // create refresh Token
    String newRefreshToken =
        this.securityUtil.createRefreshToken(email, resLoginDTO);

    // update user
    this.userServiceImpl.UpdateRefreshToken(newRefreshToken, email);

    //  set cookies
    ResponseCookie responseCookie =
        ResponseCookie.from("refresh_Token", newRefreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(refreshTokenExpiration)
            .build();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
        .body(resLoginDTO);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logOut() throws ConflictException {
    String email = SecurityUtil.getCurrentUserLogin().isPresent()
                       ? SecurityUtil.getCurrentUserLogin().get()
                       : "";

    if (email.equals("")) {
      throw new ConflictException("Access Token không hợp lệ. ");
    }
    this.userServiceImpl.UpdateRefreshToken("null", email);
    ResponseCookie responseCookie = ResponseCookie.from("refresh_Token", null)
                                        .httpOnly(true)
                                        .secure(true)
                                        .path("/")
                                        .maxAge(0)
                                        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
        .body(null);
  }

  @PostMapping("/register")
  @ApiMessage("Đăng kí tài khoản thành công. ")
  public ResponseEntity<?>
  postMethodName(@RequestBody @Valid UserReqRegister userReq)
      throws ConflictException {
    UserDto userRep = this.userServiceImpl.register(userReq);
    return ResponseEntity.ok(userRep);
  }
}
