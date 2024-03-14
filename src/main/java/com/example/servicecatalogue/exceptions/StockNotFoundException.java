package com.example.servicecatalogue.exceptions;

public class StockNotFoundException extends Throwable {
    public StockNotFoundException() {
    }

    public StockNotFoundException(String message) {
        super(message);
    }
}
