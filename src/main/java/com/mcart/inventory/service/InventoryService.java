package com.mcart.inventory.service;

import com.mcart.inventory.dto.InventoryAdjustRequest;
import com.mcart.inventory.dto.InventoryInitRequest;
import com.mcart.inventory.dto.InventoryResponse;
import com.mcart.inventory.entity.InventoryItem;
import com.mcart.inventory.exception.InventoryNotFoundException;
import com.mcart.inventory.exception.NotEnoughInventoryException;
import com.mcart.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryItem getByProductId(String productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));
    }

    /** All rows for admin dashboards (may be large in dev). */
    public List<InventoryResponse> listAll() {
        List<InventoryResponse> rows = inventoryRepository.findAll().stream()
                .map(i -> new InventoryResponse(i.getProductId(), i.getAvailableQty()))
                .sorted(Comparator.comparing(InventoryResponse::productId))
                .toList();
        log.debug("Inventory listAll rowCount={}", rows.size());
        return rows;
    }

    @Transactional
    public InventoryItem init(InventoryInitRequest req) {
        InventoryItem item = inventoryRepository.findById(req.productId()).orElseGet(InventoryItem::new);
        item.setProductId(req.productId().trim());
        item.setAvailableQty(req.availableQty());
        item.setUpdatedAt(Instant.now());
        InventoryItem saved = inventoryRepository.save(item);
        log.info("Inventory upserted productId={} availableQty={}", saved.getProductId(), saved.getAvailableQty());
        return saved;
    }

    @Transactional
    public void decrement(InventoryAdjustRequest req) {
        Instant now = Instant.now();
        String ref = req.orderId() != null ? req.orderId() : "n/a";
        for (var item : req.items()) {
            int updated = inventoryRepository.decrementIfEnough(item.productId(), item.quantity(), now);
            if (updated != 1) {
                log.warn("Inventory decrement failed ref={} productId={} requestedQty={}", ref, item.productId(), item.quantity());
                throw new NotEnoughInventoryException(item.productId());
            }
        }
        log.info("Inventory decremented ref={} lineCount={}", ref, req.items().size());
    }

    @Transactional
    public void increment(InventoryAdjustRequest req) {
        Instant now = Instant.now();
        String ref = req.orderId() != null ? req.orderId() : "n/a";
        for (var item : req.items()) {
            int updated = inventoryRepository.increment(item.productId(), item.quantity(), now);
            if (updated != 1) {
                log.warn("Inventory increment skipped missing row ref={} productId={}", ref, item.productId());
                throw new InventoryNotFoundException(item.productId());
            }
        }
        log.info("Inventory incremented ref={} lineCount={}", ref, req.items().size());
    }

    @Transactional
    public void deleteByProductId(String productId) {
        String id = productId.trim();
        inventoryRepository.deleteById(id);
        log.debug("Inventory deleteByProductId productId={} (no-op if absent)", id);
    }
}

