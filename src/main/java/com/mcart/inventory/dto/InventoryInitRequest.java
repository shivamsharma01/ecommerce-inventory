package com.mcart.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record InventoryInitRequest(
        @NotBlank String productId,
        @Min(0) int availableQty
) {
}

