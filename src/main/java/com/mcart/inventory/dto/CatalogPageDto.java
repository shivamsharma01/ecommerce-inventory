package com.mcart.inventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CatalogPageDto(
		List<CatalogProductDto> items,
		Long total,
		Integer page,
		Integer size,
		Integer totalPages
) {
}
