package com.example.servicecatalogue.exceptions;

public class StockExisteDejaException extends Throwable {
    public StockExisteDejaException() {
    }

    public StockExisteDejaException(String message) {
        super(message);
    }
}
