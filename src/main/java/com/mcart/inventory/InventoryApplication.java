package com.mcart.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Inventory microservice.
 * <p>
 * Tracks per-product available quantity and supports atomic decrement/increment operations for checkout.
 * </p>
 */
@SpringBootApplication
public class InventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }
}

