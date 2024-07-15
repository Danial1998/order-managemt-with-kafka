package com.orderManagement.service;

import com.orderManagement.model.Inventory;
import com.orderManagement.repository.InventoryRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public boolean isProductAvailable(String productCode){
        Optional<Inventory> inventory = inventoryRepository.findByProductCode(productCode);
        return inventory.isPresent() && inventory.get().getQuantity()>0;

    }
}
