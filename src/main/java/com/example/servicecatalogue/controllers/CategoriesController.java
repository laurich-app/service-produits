package com.example.servicecatalogue.controllers;

import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.services.ServiceCategorie;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

public class CategoriesController {

    @Autowired
    private final ServiceCategorie serviceCategorie;


    private final Validator validator;

    public CategoriesController(Validator validator)
    {
        this.validator = validator;
    }

    /*
        Pour créer une catégorie
     */
    @PostMapping
    public ResponseEntity<CategorieDTO> createCategory(@RequestBody CategorieDTO categorieDTO) {
        CategorieDTO createdCategorieDTO = serviceCategorie.createCategory(categorieDTO);
        return new ResponseEntity<>(createdCategorieDTO, HttpStatus.CREATED);
    }

    /*
        Pour récupérer une catégrorie
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategorieDTO> getCategoryDetails(@PathVariable int id) {
        CategorieDTO categorieDTO = serviceCategorie.getCategoryById(id);
        return new ResponseEntity<>(categorieDTO, HttpStatus.OK);
    }

    /*
        Pour récuperer la liste des categories paginées
     */
    @GetMapping
    public ResponseEntity<Paginate<CategorieOutPaginateDTO>> getAllCategories(
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
        Paginate<CategorieOutPaginateDTO> utilisateur = this.serviceCategorie.getAllCategories(paginateRequest);
        return ResponseEntity.ok(utilisateur);
    }


    /*
        Pour modifier une catégorie
    */
    @PutMapping("/{id}")
    public ResponseEntity<CategorieDTO> updateCategory(
            @PathVariable int id,
            @RequestBody CategorieDTO categorieDTO) {
        CategorieDTO updatedCategorieDTO = serviceCategorie.updateCategory(id, categorieDTO);
        return new ResponseEntity<>(updatedCategorieDTO, HttpStatus.OK);
    }

    /*
        Pour supprimer une catégorie
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        serviceCategorie.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}



}
