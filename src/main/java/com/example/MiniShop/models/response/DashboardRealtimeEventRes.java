package com.example.MiniShop.models.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardRealtimeEventRes {
  private String eventType;
  private String metric;
  private LocalDateTime occurredAt;
}
