package com.orderManagement.service;

import com.orderManagement.model.Inventory;
import com.orderManagement.repository.InventoryRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "order-topic", groupId = "inventory-group")
    public void handleOrderRequest(String productCode){
        boolean isAvailable = inventoryRepository.findByProductCode(productCode)
                .map(inventory -> inventory.getQuantity()>0)
                .orElse(false);

        // Publish availability status to Kafka
        String availabilityStatus = productCode + ":" + (isAvailable ? "available" : "not-available");
        kafkaTemplate.send("inventory-response-topic", availabilityStatus);
    }
}
