package com.mcart.inventory.service;

import com.mcart.inventory.dto.InventoryAdjustRequest;
import com.mcart.inventory.dto.InventoryInitRequest;
import com.mcart.inventory.entity.InventoryItem;
import com.mcart.inventory.exception.InventoryNotFoundException;
import com.mcart.inventory.exception.NotEnoughInventoryException;
import com.mcart.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryItem getByProductId(String productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));
    }

    @Transactional
    public InventoryItem init(InventoryInitRequest req) {
        InventoryItem item = inventoryRepository.findById(req.productId()).orElseGet(InventoryItem::new);
        item.setProductId(req.productId().trim());
        item.setAvailableQty(req.availableQty());
        item.setUpdatedAt(Instant.now());
        return inventoryRepository.save(item);
    }

    @Transactional
    public void decrement(InventoryAdjustRequest req) {
        Instant now = Instant.now();
        for (var item : req.items()) {
            int updated = inventoryRepository.decrementIfEnough(item.productId(), item.quantity(), now);
            if (updated != 1) {
                throw new NotEnoughInventoryException(item.productId());
            }
        }
    }

    @Transactional
    public void increment(InventoryAdjustRequest req) {
        Instant now = Instant.now();
        for (var item : req.items()) {
            int updated = inventoryRepository.increment(item.productId(), item.quantity(), now);
            if (updated != 1) {
                throw new InventoryNotFoundException(item.productId());
            }
        }
    }

    @Transactional
    public void deleteByProductId(String productId) {
        inventoryRepository.deleteById(productId.trim());
    }
}

