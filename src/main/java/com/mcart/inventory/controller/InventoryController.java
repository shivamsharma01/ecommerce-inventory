package com.mcart.inventory.controller;

import com.mcart.inventory.dto.InventoryAdjustRequest;
import com.mcart.inventory.dto.InventoryInitRequest;
import com.mcart.inventory.dto.InventoryResponse;
import com.mcart.inventory.entity.InventoryItem;
import com.mcart.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.mcart.inventory.config.OpenApiConfig.BEARER_JWT;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    @Operation(summary = "Get inventory for product")
    public ResponseEntity<InventoryResponse> get(@PathVariable String productId) {
        InventoryItem item = inventoryService.getByProductId(productId);
        return ResponseEntity.ok(new InventoryResponse(item.getProductId(), item.getAvailableQty()));
    }

    @PostMapping("/init")
    @Operation(summary = "Initialize inventory for a product")
    @SecurityRequirement(name = BEARER_JWT)
    @PreAuthorize("hasAuthority('SCOPE_product.admin')")
    public ResponseEntity<InventoryResponse> init(@Valid @RequestBody InventoryInitRequest req) {
        InventoryItem item = inventoryService.init(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new InventoryResponse(item.getProductId(), item.getAvailableQty()));
    }

    @PostMapping("/decrement")
    @Operation(summary = "Decrement inventory (checkout)")
    @SecurityRequirement(name = BEARER_JWT)
    public ResponseEntity<Void> decrement(@Valid @RequestBody InventoryAdjustRequest req) {
        inventoryService.decrement(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/increment")
    @Operation(summary = "Increment inventory (rollback)")
    @SecurityRequirement(name = BEARER_JWT)
    public ResponseEntity<Void> increment(@Valid @RequestBody InventoryAdjustRequest req) {
        inventoryService.increment(req);
        return ResponseEntity.noContent().build();
    }
}

