package com.example.MiniShop.models.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardNewUsersPointRes {
  private String period;
  private long newUsers;
}
