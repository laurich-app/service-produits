package com.example.servicecatalogue.exceptions;

public class StockNotFoundException extends Exception {
    public StockNotFoundException() {
    }

    public StockNotFoundException(String message) {
        super(message);
    }
}
