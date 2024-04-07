package com.example.servicecatalogue.services;

import com.example.servicecatalogue.dtos.CategorieDTO;
import com.example.servicecatalogue.dtos.pagination.Paginate;
import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.dtos.pagination.Pagination;
import com.example.servicecatalogue.dtos.rabbits.SupprimerStockDTO;
import com.example.servicecatalogue.exceptions.CategorieNotFoundException;
import com.example.servicecatalogue.modele.Categorie;
import com.example.servicecatalogue.repositories.CategorieRepository;
import com.example.servicecatalogue.repositories.ProduitRepository;
import com.example.servicecatalogue.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ServiceCategorie {

    public static final String CATEGORY_NOT_FOUND_WITH_ID = "Category not found with id: ";
    private final CategorieRepository categorieRepository;

    private final ProduitRepository produitRepository;

    private final ServiceRabbitMQSender serviceRabbitMQSender;

    public ServiceCategorie(@Autowired ServiceRabbitMQSender serviceRabbitMQSender, @Autowired CategorieRepository categorieRepository, @Autowired ProduitRepository produitRepository) {
        this.serviceRabbitMQSender = serviceRabbitMQSender;
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
    }

    public CategorieDTO createCategory(CategorieDTO categorieDTO) {
        if (categorieDTO.getLibelle() == null || categorieDTO.getLibelle().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'libelle' ne doit pas être nul ou vide");
        }
        Categorie newCategory = new Categorie();
        newCategory.setLibelle(categorieDTO.getLibelle());
        Categorie savedCategory = categorieRepository.save(newCategory);
        int nbProduits = produitRepository.countByCategory(savedCategory);
        return new CategorieDTO(savedCategory.getIdCategorie(), savedCategory.getLibelle(), nbProduits);
    }

    public CategorieDTO getCategoryById(int categoryId) throws CategorieNotFoundException {
        Optional<Categorie> categorieOptional = categorieRepository.findById(categoryId);

        if (categorieOptional.isPresent()) {
            Categorie categorie = categorieOptional.get();
            int nbProduits = produitRepository.countByCategory(categorie);

            return new CategorieDTO(categorie.getIdCategorie(), categorie.getLibelle(), nbProduits);
        } else {
            throw new CategorieNotFoundException(CATEGORY_NOT_FOUND_WITH_ID + categoryId);
        }
    }

    public Paginate<CategorieDTO> getAllCategories(PaginateRequestDTO paginateRequestDTO){
        Pageable pageable = PageableUtils.convert(paginateRequestDTO);
        Page<Categorie> paginated = this.categorieRepository.findAll(pageable);
        List<CategorieDTO> dtos = paginated.stream()
                .map(e -> {
                    int nbProduits = produitRepository.countByCategory(e);
                    return Categorie.toDTO(e, nbProduits);
                })
                .toList();
        return new Paginate<>(dtos, new Pagination(Math.toIntExact(paginated.getTotalElements()),
                paginateRequestDTO.limit(), paginateRequestDTO.page()));
    }

    public CategorieDTO updateCategory(int id, CategorieDTO categorieDTO) throws CategorieNotFoundException {
        Optional<Categorie> categorieOptional = categorieRepository.findById(id);

        if (categorieOptional.isPresent()) {
            Categorie existingCategory = categorieOptional.get();
            existingCategory.setLibelle(categorieDTO.getLibelle());
            Categorie updatedCategory = categorieRepository.save(existingCategory);
            int nbProduits = produitRepository.countByCategory(updatedCategory);
            return new CategorieDTO(updatedCategory.getIdCategorie(), updatedCategory.getLibelle(), nbProduits);
        } else {
            throw new CategorieNotFoundException(CATEGORY_NOT_FOUND_WITH_ID + id);
        }
    }

    public void deleteCategory(int id) throws CategorieNotFoundException {
        Optional<Categorie> categorieOptional = categorieRepository.findById(id);
        if (categorieOptional.isPresent()) {
            Categorie category = categorieOptional.get();
            category.getProduit().forEach(p -> {
                p.getStocks().forEach(s ->
                    this.serviceRabbitMQSender.supprimerStock(new SupprimerStockDTO(s.getCouleurs().name(), p.getId()))
                );
                produitRepository.delete(p);
            });
            categorieRepository.delete(category);
        } else {
            throw new CategorieNotFoundException(CATEGORY_NOT_FOUND_WITH_ID + id);
        }
    }

}

