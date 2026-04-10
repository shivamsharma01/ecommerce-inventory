package com.mcart.inventory.exception;

public class NotEnoughInventoryException extends RuntimeException {
    public NotEnoughInventoryException(String productId) {
        super("Not enough inventory for productId=" + productId);
    }
}

