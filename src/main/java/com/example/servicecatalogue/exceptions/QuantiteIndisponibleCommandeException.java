package com.example.servicecatalogue.exceptions;

public class QuantiteIndisponibleCommandeException extends Exception {
    public QuantiteIndisponibleCommandeException() {
    }

    public QuantiteIndisponibleCommandeException(String message) {
        super(message);
    }
}
