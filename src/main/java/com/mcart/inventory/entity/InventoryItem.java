package com.mcart.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "inventory")
public class InventoryItem {

    @Id
    @Column(name = "product_id", nullable = false, length = 64)
    private String productId;

    @Column(name = "available_qty", nullable = false)
    private int availableQty;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public InventoryItem() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(int availableQty) {
        this.availableQty = availableQty;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

