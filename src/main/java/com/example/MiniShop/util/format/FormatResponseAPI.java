package com.example.MiniShop.util.format;

import com.example.MiniShop.models.response.ApiResponse;
import com.example.MiniShop.util.annotation.ApiMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class FormatResponseAPI implements ResponseBodyAdvice {

  @Override
  public boolean supports(MethodParameter returnType, Class converterType) {
    return true;
  }

  @Override
  public Object
  beforeBodyWrite(Object body, MethodParameter returnType,
                  MediaType selectedContentType, Class selectedConverterType,
                  ServerHttpRequest request, ServerHttpResponse response) {

    HttpServletResponse servletResponse =
        ((ServletServerHttpResponse)response).getServletResponse();
    int status = servletResponse.getStatus();

    ApiResponse<Object> res = new ApiResponse<Object>();

    res.setStatusCode(status);

    if (body instanceof String) {
      return body;
    }
    String path = request.getURI().getPath();
    if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
      return body;
    }

    if (status >= 400) {
      return body;
    } else {
      res.setData(body);
      ApiMessage mess = returnType.getMethodAnnotation(ApiMessage.class);
      res.setMessage(mess == null ? "call api success" : mess.value());
    }

    return res;
  }
}
