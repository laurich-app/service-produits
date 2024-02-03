package com.example.servicecatalogue.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/produits")
public class ProduitsController {

    @GetMapping
    public ResponseEntity get() {
        // TO DO
        return ResponseEntity.ok().build();
    }
}
