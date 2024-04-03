package com.example.servicecatalogue.exceptions;

public class CategorieNotFoundException extends Exception{
    public CategorieNotFoundException() {
    }

    public CategorieNotFoundException(String message) {
        super(message);
    }
}
