package com.example.MiniShop.exception.handler;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.response.ApiResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  // 409 Conflict
  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiResponse<Object>>
  handleIdInvalidException(ConflictException ex) {
    ApiResponse<Object> res = new ApiResponse<>();
    res.setStatusCode(HttpStatus.CONFLICT.value());
    res.setError("Conflict");
    res.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
  }

  // 400 Bad Request
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>>
  validationError(MethodArgumentNotValidException ex) {

    BindingResult result = ex.getBindingResult();
    List<FieldError> fieldErrors = result.getFieldErrors();

    ApiResponse<Object> res = new ApiResponse<>();
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setError("Bad Request");
    List<String> errors =
        fieldErrors.stream().map(FieldError::getDefaultMessage).toList();

    res.setMessage(errors.size() > 1 ? errors : errors.get(0));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }

  // 404 Not Found
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiResponse<Object>>
  handleNotFound(NotFoundException ex) {
    ApiResponse<Object> res = new ApiResponse<>();
    res.setStatusCode(HttpStatus.NOT_FOUND.value());
    res.setError("Not Found");
    res.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
  }
}
