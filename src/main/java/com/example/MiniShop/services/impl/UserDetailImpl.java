package com.example.MiniShop.services.impl;


import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.MiniShop.services.UserService;

@Component("userDetailsService")
public class UserDetailImpl implements UserDetailsService {
  private final UserService userService;

  public UserDetailImpl(UserServiceImpl userService) {
    this.userService = userService;
  }

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {
    com.example.MiniShop.models.entity.User user =
        this.userService.fetchByEmail(username);

    if (user == null) {
      throw new UsernameNotFoundException("tài khoản không tồn tại");
    }
    return new User(
        user.getEmail(), user.getPassword(),
        Collections.singletonList(new SimpleGrantedAuthority("USER")));
  }
}
