package com.example.MiniShop.config;

import com.example.MiniShop.exception.custom.InvalidException;
import com.example.MiniShop.models.entity.Permission;
import com.example.MiniShop.models.entity.Role;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.services.UserService;
import com.example.MiniShop.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

public class PermissionInterceptor implements HandlerInterceptor {
  @Autowired UserService userService;

  @Transactional
  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler)
      throws Exception {

    String path = (String)request.getAttribute(
        HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
    String requestURL = request.getRequestURI();
    String method = request.getMethod();
    System.out.println("👉 Interceptor: preHandle");

    // check permission
    String email = SecurityUtil.getCurrentUserLogin().isPresent()
                       ? SecurityUtil.getCurrentUserLogin().get()
                       : "";

    // get user
    if (email != null && !email.isEmpty()) {
      User user = this.userService.fetchByEmail(email);
      if (user != null) {
        Role role = user.getRole();
        if (role != null) {
          List<Permission> permissions = role.getPermissions();

          boolean isAllow = permissions.stream().allMatch(
              item
              -> item.getApiPath().equals(path) &&
                     item.getMethod().equals(method));

          if (isAllow == false) {
            throw new InvalidException("Bạn không có quyền truy cập api này. ");
          }
        } else {
          throw new InvalidException("Bạn không có quyền truy cập api này. ");
        }
      }
    }
    return true;
  }
}
