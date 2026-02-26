package com.example.MiniShop.models.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ApiResponsePagination {
  private Meta meta;
  private Object result;

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Meta {
    private int pageCurrent;
    private int pageSize;
    private int pages;
    private long total;
  }
}
