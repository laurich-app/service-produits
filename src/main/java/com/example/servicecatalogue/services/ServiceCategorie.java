package com.example.servicecatalogue.services;

import com.example.servicecatalogue.dtos.CategorieDTO;
import com.example.servicecatalogue.dtos.out.CategorieOutPaginateDTO;
import com.example.servicecatalogue.dtos.pagination.Paginate;
import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.dtos.pagination.Pagination;
import com.example.servicecatalogue.modele.Categorie;
import com.example.servicecatalogue.repositories.CategorieRepository;
import com.example.servicecatalogue.repositories.ProduitRepository;
import com.example.servicecatalogue.utils.PageableUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


    @Service
    public class ServiceCategorie {


        @Autowired
        private CategorieRepository categorieRepository;

        @Autowired
        private ProduitRepository produitRepository;


        public CategorieDTO createCategory(CategorieDTO categorieDTO) {
            Categorie newCategory = new Categorie();
            newCategory.setLibelle(categorieDTO.getLibelle());
            Categorie savedCategory = categorieRepository.save(newCategory);
            int nbProduits = produitRepository.countByCategory(savedCategory);
            return new CategorieDTO(savedCategory.getId_categorie(), savedCategory.getLibelle(), nbProduits);
        }

        public CategorieDTO getCategoryById(int categoryId) {
            Optional<Categorie> categorieOptional = categorieRepository.findById(categoryId);

            if (categorieOptional.isPresent()) {
                Categorie categorie = categorieOptional.get();
                int nbProduits = produitRepository.countByCategory(categorie);

                return new CategorieDTO(categorie.getId_categorie(), categorie.getLibelle(), nbProduits);
            } else {
                throw new EntityNotFoundException("Category not found with id: " + categoryId);
            }
        }

        public Paginate<CategorieOutPaginateDTO> getAllCategories(PaginateRequestDTO paginateRequestDTO){
            Pageable pageable = PageableUtils.convert(paginateRequestDTO);
            Page<Categorie> paginated = this.categorieRepository.findAll(pageable);
            List<CategorieOutPaginateDTO> dtos = paginated.stream()
                    .map(Categorie::toDTO)
                    .collect(Collectors.toList());
            Paginate<CategorieOutPaginateDTO> paginate = new Paginate<>(dtos, new Pagination(Math.toIntExact(paginated.getTotalElements()),
                    paginateRequestDTO.limit(), paginateRequestDTO.page()));
            return paginate;
        };

        public CategorieDTO updateCategory(int id, CategorieDTO categorieDTO) {
            Optional<Categorie> categorieOptional = categorieRepository.findById(id);

            if (categorieOptional.isPresent()) {
                Categorie existingCategory = categorieOptional.get();
                existingCategory.setLibelle(categorieDTO.getLibelle());
                Categorie updatedCategory = categorieRepository.save(existingCategory);
                int nbProduits = produitRepository.countByCategory(updatedCategory);
                return new CategorieDTO(updatedCategory.getId_categorie(), updatedCategory.getLibelle(), nbProduits);
            } else {
                throw new EntityNotFoundException("Category not found with id: " + id);
            }
        }

        public void deleteCategory(int id) {
            Optional<Categorie> categorieOptional = categorieRepository.findById(id);
            if (categorieOptional.isPresent()) {
                Categorie category = categorieOptional.get();
                produitRepository.deleteByCategory(category);
                categorieRepository.delete(category);
            } else {
                throw new EntityNotFoundException("Category not found with id: " + id);
            }
        }

    }

