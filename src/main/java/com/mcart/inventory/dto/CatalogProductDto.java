package com.mcart.inventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Subset of product API JSON for catalog sync.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CatalogProductDto(String id, Integer stockQuantity) {
}
