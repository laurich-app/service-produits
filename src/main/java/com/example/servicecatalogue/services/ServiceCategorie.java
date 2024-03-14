package com.example.servicecatalogue.services;

import com.example.servicecatalogue.dtos.CategorieDTO;
import com.example.servicecatalogue.dtos.pagination.Paginate;
import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.dtos.pagination.Pagination;
import com.example.servicecatalogue.dtos.rabbits.GenererCommandeDTO;
import com.example.servicecatalogue.dtos.rabbits.SupprimerStockDTO;
import com.example.servicecatalogue.modele.Categorie;
import com.example.servicecatalogue.modele.Produit;
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

        private final ServiceRabbitMQSender serviceRabbitMQSender;

        public ServiceCategorie(@Autowired ServiceRabbitMQSender serviceRabbitMQSender) {
            this.serviceRabbitMQSender = serviceRabbitMQSender;
        }

        public CategorieDTO createCategory(CategorieDTO categorieDTO) {
            if (categorieDTO.getLibelle() == null || categorieDTO.getLibelle().isEmpty()) {
                throw new IllegalArgumentException("Le champ 'libelle' ne doit pas être nul ou vide");
            }
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

        public Paginate<CategorieDTO> getAllCategories(PaginateRequestDTO paginateRequestDTO){
            Pageable pageable = PageableUtils.convert(paginateRequestDTO);
            Page<Categorie> paginated = this.categorieRepository.findAll(pageable);
            List<CategorieDTO> dtos = paginated.stream()
                    .map((e) -> {
                        int nbProduits = produitRepository.countByCategory(e);
                        return Categorie.toDTO(e, nbProduits);
                    })
                    .collect(Collectors.toList());
            Paginate<CategorieDTO> paginate = new Paginate<>(dtos, new Pagination(Math.toIntExact(paginated.getTotalElements()),
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
                category.getProduit().forEach(p -> {
                    p.getStocks().forEach(s -> {
                        this.serviceRabbitMQSender.supprimerStock(new SupprimerStockDTO(s.getCouleurs().name(), p.getId()));
                    });
                    produitRepository.delete(p);
                });
                categorieRepository.delete(category);
            } else {
                throw new EntityNotFoundException("Category not found with id: " + id);
            }
        }

    }

