package com.example.MiniShop.services.impl;

import com.example.MiniShop.models.response.DashboardRealtimeEventRes;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardRealtimeNotifier {
  private static final String[] TOPICS = {
      "/topic/dashboard/stats",
      "/topic/dashboard/orders/by-status",
      "/topic/dashboard/revenue",
      "/topic/dashboard/products/top",
      "/topic/dashboard/inventory/low-stock",
      "/topic/dashboard/profit",
      "/topic/dashboard/purchase-cost",
      "/topic/dashboard/users/new"
  };

  private final SimpMessagingTemplate messagingTemplate;

  public void publishAll(@NonNull String eventType) {
    for (String topic : TOPICS) {
      if (topic == null) {
        continue;
      }
      messagingTemplate.convertAndSend(topic, buildEvent(eventType, topic));
    }
  }

  @NonNull
  private DashboardRealtimeEventRes buildEvent(@NonNull String eventType,
                                               @NonNull String topic) {
    DashboardRealtimeEventRes event = new DashboardRealtimeEventRes();
    event.setEventType(eventType);
    event.setMetric(topic);
    event.setOccurredAt(LocalDateTime.now());
    return event;
  }
}
