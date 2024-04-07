package com.example.servicecatalogue.controllers;

import com.example.servicecatalogue.dtos.ProduitDTO;
import com.example.servicecatalogue.dtos.ProduitUpdateDTO;
import com.example.servicecatalogue.dtos.StockDTO;
import com.example.servicecatalogue.dtos.out.ProduitOutDTO;
import com.example.servicecatalogue.dtos.out.ProduitOutPaginateDTO;
import com.example.servicecatalogue.dtos.out.StocksOutDTO;
import com.example.servicecatalogue.dtos.pagination.Paginate;
import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.enums.Couleurs;
import com.example.servicecatalogue.exceptions.StockExisteDejaException;
import com.example.servicecatalogue.modele.Produit;
import com.example.servicecatalogue.modele.Stocks;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    public ResponseEntity<ProduitOutDTO> saveProduit(@RequestBody ProduitDTO produitDTO) {
        try {
            Produit savedProduit = serviceProduit.saveProduit(produitDTO);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(savedProduit.getId()).toUri();
            return ResponseEntity.created(location).body(ProduitOutDTO.fromProduit(savedProduit));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /*
        Récupérer un produit par son id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProduitOutDTO> getProduitById(@PathVariable int id){
        try{
            Produit produit = serviceProduit.getProduitById(id);
            return ResponseEntity.ok(ProduitOutDTO.fromProduit(produit));
        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    /*
        Supprimer un produit par son id
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    public ResponseEntity<String> deleteProduit(@PathVariable int id) {
        try {
            serviceProduit.deleteProduit(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /*
       Modifier un produit
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    public ResponseEntity<ProduitOutDTO> updateProduit(@PathVariable int id, @RequestBody ProduitUpdateDTO produitDTO) {
        try {
            return ResponseEntity.ok(ProduitOutDTO.fromProduit(serviceProduit.updateProduit(id,produitDTO)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
        Paginate<ProduitOutPaginateDTO> allProduitsPaginate = this.serviceProduit.getAllProduits(paginateRequest);
        return ResponseEntity.ok(allProduitsPaginate);
    }

    /*
        Supprimer un stock par son id produit et sa couleur
     */
    @DeleteMapping("/{id}/couleurs/{couleur}")
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    public ResponseEntity<String> deleteStock(@PathVariable int id, @PathVariable Couleurs couleur) {
        try {
            serviceProduit.deleteStock(id, couleur);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/couleurs")
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    public ResponseEntity<StocksOutDTO> addStock(@PathVariable int id, @RequestBody StockDTO stockDTO) {
        try {
            Stocks s = serviceProduit.addStock(id, stockDTO.couleur());
            return ResponseEntity.status(HttpStatus.CREATED).body(StocksOutDTO.fromStock(s));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (StockExisteDejaException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
