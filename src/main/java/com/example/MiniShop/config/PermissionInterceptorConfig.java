// package com.example.MiniShop.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class PermissionInterceptorConfig implements WebMvcConfigurer {
//   @Bean
//   public PermissionInterceptor getPermissionInterceptor() {
//     return new PermissionInterceptor();
//   }

//   @Override
//   public void addInterceptors(InterceptorRegistry registry) {
//     String[] whiteList = {"/", "/api/v1/auth/login", "/api/v1/auth/register",
//                           "/api/v1/auth/refresh"};
//     registry.addInterceptor(getPermissionInterceptor())
//         .excludePathPatterns(whiteList);
//   }
// }
