package com.example.MiniShop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
  @Bean
  SecurityFilterChain
  filterChain(HttpSecurity http,
              CustomAuthenticationEntryPoint customAuthenticationEntryPoint)
      throws Exception {
    http.csrf(c -> c.disable())
        .authorizeHttpRequests(
            authorize
            -> authorize.requestMatchers("/", "/api/v1/auth/login","/api/v1/auth/register", "/api/v1/auth/refresh")
                   .permitAll()
                   .anyRequest()
                   .authenticated())
        .formLogin(formLogin -> formLogin.disable())
        .oauth2ResourceServer((oauth2)
                                  -> oauth2.jwt(Customizer.withDefaults())
                                         .authenticationEntryPoint(
                                             customAuthenticationEntryPoint))
        // .exceptionHandling(
        //     exceptions
        //     -> exceptions
        //            .authenticationEntryPoint(
        //                new BearerTokenAuthenticationEntryPoint()) 
        //            .accessDeniedHandler(
        //                new BearerTokenAccessDeniedHandler()))

        .sessionManagement((sessionManagement)
                               -> sessionManagement.sessionCreationPolicy(
                                   SessionCreationPolicy.STATELESS));
    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter =
        new JwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthorityPrefix("");
    grantedAuthoritiesConverter.setAuthoritiesClaimName("user");

    JwtAuthenticationConverter jwtAuthenticationConverter =
        new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
        grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }
}