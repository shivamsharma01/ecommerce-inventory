package com.mcart.inventory.dto;

public record InventoryResponse(
        String productId,
        int availableQty
) {
}

