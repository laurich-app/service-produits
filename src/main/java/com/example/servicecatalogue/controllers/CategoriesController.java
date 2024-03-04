package com.example.servicecatalogue.controllers;

import com.example.servicecatalogue.dtos.CategorieDTO;
import com.example.servicecatalogue.dtos.out.CategorieOutPaginateDTO;
import com.example.servicecatalogue.dtos.pagination.Paginate;
import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.services.ServiceCategorie;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

public class CategoriesController {

    @Autowired
    private ServiceCategorie serviceCategorie;


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
        Pour récupérer une catégorie par id
    */
    @GetMapping("/{id}")
    public ResponseEntity<CategorieDTO> getCategoryDetails(@PathVariable int id) {
        try {
            CategorieDTO categorieDTO = serviceCategorie.getCategoryById(id);
            return new ResponseEntity<>(categorieDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /*
        Pour modifier une une catégorie
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategorieDTO> updateCategory(@PathVariable int id, @RequestBody CategorieDTO categorieDTO) {
        try {
            CategorieDTO updatedCategoryDTO = serviceCategorie.updateCategory(id, categorieDTO);
            return new ResponseEntity<>(updatedCategoryDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /*
        Pour supprimer une catégorie
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable int id) {
        try {
            serviceCategorie.deleteCategory(id);
            return new ResponseEntity<>("Category with id " + id + " deleted successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Category not found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }
}
