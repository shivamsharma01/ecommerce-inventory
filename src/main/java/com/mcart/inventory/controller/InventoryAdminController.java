package com.mcart.inventory.controller;

import com.mcart.inventory.service.CatalogInventorySyncService;
import com.mcart.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcart.inventory.dto.InventoryResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SCOPE_product.admin')")
public class InventoryAdminController {

	private final CatalogInventorySyncService catalogInventorySyncService;
	private final InventoryService inventoryService;

	@GetMapping("/items")
	public ResponseEntity<List<InventoryResponse>> listItems() {
		return ResponseEntity.ok(inventoryService.listAll());
	}

	@PostMapping("/sync-from-catalog")
	public ResponseEntity<Map<String, Object>> syncFromCatalog() {
		int n = catalogInventorySyncService.syncAllFromCatalog();
		return ResponseEntity.ok(Map.of("productsSynced", n));
	}
}
