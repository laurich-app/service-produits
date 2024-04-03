package com.example.servicecatalogue.exceptions;

public class StockExisteDejaException extends Exception {
    public StockExisteDejaException() {
    }

    public StockExisteDejaException(String message) {
        super(message);
    }
}
