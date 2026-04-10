package com.mcart.inventory.controller;

import com.mcart.inventory.service.CatalogInventorySyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/inventory/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SCOPE_product.admin')")
public class InventoryAdminController {

	private final CatalogInventorySyncService catalogInventorySyncService;

	@PostMapping("/sync-from-catalog")
	public ResponseEntity<Map<String, Object>> syncFromCatalog() {
		int n = catalogInventorySyncService.syncAllFromCatalog();
		return ResponseEntity.ok(Map.of("productsSynced", n));
	}
}
