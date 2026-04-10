package com.mcart.inventory.controller;

import com.mcart.inventory.exception.InventoryNotFoundException;
import com.mcart.inventory.exception.NotEnoughInventoryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<String> handleNotFound(InventoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NotEnoughInventoryException.class)
    public ResponseEntity<String> handleNotEnough(NotEnoughInventoryException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}

