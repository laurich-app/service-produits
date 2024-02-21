package com.example.servicecatalogue.controllers;

import com.example.servicecatalogue.dtos.ProduitDTO;
import com.example.servicecatalogue.dtos.out.ProduitOutPaginateDTO;
import com.example.servicecatalogue.dtos.pagination.Paginate;
import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.modele.Produit;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.servicecatalogue.services.ServiceProduit;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/produits")
public class ProduitsController {

    private final ServiceProduit serviceProduit;

    private final Validator validator;

    public ProduitsController(@Autowired ServiceProduit serviceProduit, @Autowired Validator validator) {
        this.serviceProduit = serviceProduit;
        this.validator = validator;
    }

    /*
        Récupérer un produit par son id
     */
    @GetMapping("/{id}")
    public Produit getProduitById(@PathVariable int id) {
        return serviceProduit.getProduitById(id).orElse(null);
    }

    /*
        Supprimer un produit par son id
     */
    @DeleteMapping("/{id}")
    public void deleteProduit(@PathVariable int id) {
        serviceProduit.deleteProduit(id);
    }

    /*
       Modifier un produit
     */
    @PutMapping("/produit/{id}")
    public ResponseEntity<Produit> updateProduit(@PathVariable int id, @RequestBody ProduitDTO produitDTO) {
        return ResponseEntity.ok(serviceProduit.updateProduit(id,produitDTO));
    }

    @GetMapping
    public ResponseEntity<Paginate<ProduitOutPaginateDTO>> getAllProduits(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "sortDirection", required = false) Sort.Direction sortDirection
    ) {
        PaginateRequestDTO paginateRequest = new PaginateRequestDTO(page, limit, sort, sortDirection);
        Set<ConstraintViolation<PaginateRequestDTO>> violations = this.validator.validate(paginateRequest);
        if(!violations.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Paginate<ProduitOutPaginateDTO> utilisateur = this.serviceProduit.getAllProduits(paginateRequest);
        return ResponseEntity.ok(utilisateur);
    }

    /*@PostMapping
    public Produit saveOrUpdateProduit(@RequestBody Produit produit) {
        return serviceProduit.saveOrUpdateProduit(produit);
    }*/





}
