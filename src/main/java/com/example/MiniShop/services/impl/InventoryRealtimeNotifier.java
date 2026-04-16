package com.example.MiniShop.services.impl;

import com.example.MiniShop.models.entity.Inventory;
import com.example.MiniShop.models.response.InventoryAvailabilityWsRes;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryRealtimeNotifier {
  private final SimpMessagingTemplate messagingTemplate;

  public void notifyAvailability(Long productId, Inventory inventory,
                                 String eventType) {
    if (productId == null || inventory == null) {
      return;
    }

    InventoryAvailabilityWsRes payload = new InventoryAvailabilityWsRes();
    payload.setProductId(productId);
    payload.setStock(inventory.getStock());
    payload.setReservedStock(inventory.getReserved_stock());
    payload.setAvailableStock(inventory.getStock() - inventory.getReserved_stock());
    payload.setEventType(eventType);
    payload.setUpdatedAt(LocalDateTime.now());

    messagingTemplate.convertAndSend("/topic/inventory/availability", payload);
  }
}