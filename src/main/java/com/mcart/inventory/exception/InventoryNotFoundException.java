package com.mcart.inventory.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String productId) {
        super("Inventory not initialized for productId=" + productId);
    }
}

