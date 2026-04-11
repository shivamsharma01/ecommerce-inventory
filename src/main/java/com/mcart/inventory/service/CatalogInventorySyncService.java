package com.mcart.inventory.service;

import com.mcart.inventory.dto.CatalogPageDto;
import com.mcart.inventory.dto.CatalogProductDto;
import com.mcart.inventory.dto.InventoryInitRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Pulls catalog pages from the product service and upserts inventory rows from {@code stockQuantity}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogInventorySyncService {

	private final InventoryService inventoryService;
	private final RestClient rest = RestClient.create();

	@Value("${inventory.product.base-url}")
	private String productBaseUrl;

	public int syncAllFromCatalog() {
		log.info("Starting catalog → inventory sync");
		int synced = 0;
		int page = 0;
		final int size = 100;
		while (true) {
			String uri = productBaseUrl + "/api/products?page=" + page + "&size=" + size;
			CatalogPageDto pageDto = rest.get()
					.uri(uri)
					.retrieve()
					.body(CatalogPageDto.class);
			if (pageDto == null || pageDto.items() == null || pageDto.items().isEmpty()) {
				break;
			}
			for (CatalogProductDto p : pageDto.items()) {
				if (p.id() == null || p.id().isBlank()) {
					continue;
				}
				Integer sq = p.stockQuantity();
				if (sq == null) {
					continue;
				}
				int qty = Math.max(0, sq);
				inventoryService.init(new InventoryInitRequest(p.id().trim(), qty));
				synced++;
			}
			Integer totalPages = pageDto.totalPages();
			if (totalPages == null || page + 1 >= totalPages) {
				break;
			}
			page++;
		}
		log.info("Catalog inventory sync upserted {} rows", synced);
		return synced;
	}
}
