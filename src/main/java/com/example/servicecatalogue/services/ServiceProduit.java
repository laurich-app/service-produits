package com.example.servicecatalogue.services;

import com.example.servicecatalogue.dtos.ProduitDTO;
import com.example.servicecatalogue.dtos.out.ProduitOutPaginateDTO;
import com.example.servicecatalogue.dtos.pagination.Paginate;
import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.dtos.pagination.Pagination;
import com.example.servicecatalogue.enums.Sexe;
import com.example.servicecatalogue.enums.Taille;
import com.example.servicecatalogue.modele.Categorie;
import com.example.servicecatalogue.modele.Produit;
import com.example.servicecatalogue.repositories.CategorieRepository;
import com.example.servicecatalogue.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.servicecatalogue.repositories.ProduitRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceProduit {

    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;

    public ServiceProduit(@Autowired ProduitRepository produitRepository, CategorieRepository categorieRepository) {
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
    }

    /**
     * * @param paginateRequestDTO
     * @return
     */
    public Paginate<ProduitOutPaginateDTO> getAllProduits(PaginateRequestDTO paginateRequestDTO){
        Pageable pageable = PageableUtils.convert(paginateRequestDTO);
        Page<Produit> paginated = this.produitRepository.findAll(pageable);

        // Convertir les objets BlogDAO en BlogDTO en utilisant la fabrique
        List<ProduitOutPaginateDTO> dtos = paginated.stream()
                .map(Produit::toDTO)
                .collect(Collectors.toList());

        // Créer un objet Paginate contenant les blogs paginés
        Paginate<ProduitOutPaginateDTO> paginate = new Paginate<>(dtos, new Pagination(Math.toIntExact(paginated.getTotalElements()),
                paginateRequestDTO.limit(), paginateRequestDTO.page()));

        // Retourner la liste des objets Paginate
        return paginate;
    };

    public Optional<Produit> getProduitById(int id) {
        return produitRepository.findById(id);
    }

    public Produit saveOrUpdateProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    public void deleteProduit(int id) {
        produitRepository.deleteById(id);
    }


    public Produit updateProduit(int id ,ProduitDTO produitDTO) {
        Optional<Produit> opProduit = produitRepository.findById(id);
        if(opProduit.isEmpty()) {
            throw new IllegalArgumentException("Produit not found");
        }
            Produit produit = opProduit.get();
            // couleurs à voir plus tard
            produit.setPrix_unitaire(produitDTO.getPrix_unitaire());
            produit.setSexe(Sexe.valueOf(produitDTO.getSexe()));
            produit.setTaille(Taille.valueOf(produitDTO.getTaille()));
            produit.setLibelle(produitDTO.getLibelle());
            produit.setImage(produitDTO.getImage());
            produit.setDescription(produitDTO.getDescription());

            Optional<Categorie> categorie = categorieRepository.findById(produitDTO.getId_categorie());
            if(categorie.isEmpty()) {
                throw new IllegalArgumentException("Categorie not found");
            }
            produit.setCategory(categorie.get());
            return produitRepository.save(produit);
    }
}
