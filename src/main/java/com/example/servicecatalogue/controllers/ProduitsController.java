package com.example.servicecatalogue.controllers;

import com.example.servicecatalogue.dtos.ProduitDTO;
import com.example.servicecatalogue.dtos.out.ProduitOutPaginateDTO;
import com.example.servicecatalogue.dtos.pagination.Paginate;
import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.modele.Produit;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.servicecatalogue.services.ServiceProduit;

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

    @PostMapping()
    public ResponseEntity<Produit> saveProduit(@RequestBody ProduitDTO produitDTO) {
        try {
            Produit savedProduit = serviceProduit.saveProduit(produitDTO);
            return new ResponseEntity<>(savedProduit, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /*
        Récupérer un produit par son id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable int id){
        try{
            Produit produit = serviceProduit.getProduitById(id);
            return new ResponseEntity<>(produit, HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /*
        Supprimer un produit par son id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduit(@PathVariable int id) {
        try {
            String res= serviceProduit.deleteProduit(id);
            return new ResponseEntity<>(res,HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /*
       Modifier un produit
     */
    @PutMapping("/{id}")
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
}
