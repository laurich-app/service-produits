package com.example.servicecatalogue.services;

import com.example.servicecatalogue.dtos.ProduitDTO;
import com.example.servicecatalogue.dtos.out.ProduitOutPaginateDTO;
import com.example.servicecatalogue.dtos.pagination.Paginate;
import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.dtos.pagination.Pagination;
import com.example.servicecatalogue.enums.Couleurs;
import com.example.servicecatalogue.enums.Sexe;
import com.example.servicecatalogue.enums.Taille;
import com.example.servicecatalogue.modele.Categorie;
import com.example.servicecatalogue.modele.Produit;
import com.example.servicecatalogue.repositories.CategorieRepository;
import com.example.servicecatalogue.utils.PageableUtils;
import jakarta.persistence.EntityNotFoundException;
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
    public Produit saveProduit(ProduitDTO produitDTO){
        if(produitDTO.getPrix_unitaire()<=0){
            throw new IllegalArgumentException("Le prix unitaire doit être positif");
        }
        Categorie categorie = categorieRepository.findById(produitDTO.getId_categorie())
                .orElseThrow(()-> new IllegalArgumentException("La catégorie spécifié n'existe pas"));
        String libelleMajuscules = produitDTO.getLibelle().toUpperCase();
        Produit produit = new Produit();
        produit.setPrix_unitaire(produitDTO.getPrix_unitaire());
        produit.setSexe(Sexe.valueOf(produitDTO.getSexe()));
        produit.setTaille(Taille.valueOf(produitDTO.getTaille()));
       /* produit.setCouleurs(
                produitDTO.getCouleurs()
                        .stream()
                        .map(Couleurs::valueOf)
                        .collect(Collectors.toList())
        );*/
        produit.setLibelle(libelleMajuscules);
        produit.setDescription(produitDTO.getDescription());
       // produit.setStock(produitDTO.getStock());
        produit.setImage(produitDTO.getImage());
        produit.setCategory(categorie);
        return produitRepository.save(produit);

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

    public Produit getProduitById(int id) {
        Optional<Produit> produitOptional = produitRepository.findById(id);

        if (produitOptional.isEmpty()) {
            throw new EntityNotFoundException("Produit non trouvé pour l'ID :" +id);
        }
        return produitOptional.get();
    }

    public String deleteProduit(int id) {
        if (!produitRepository.existsById(id)) {
            throw new EntityNotFoundException("Produit non trouvé pour l'ID :" +id);
        }
        produitRepository.deleteById(id);
        return "Produit supprimé !";
    }

    public Produit updateProduit(int id ,ProduitDTO produitDTO) {
        Optional<Produit> opProduit = produitRepository.findById(id);
        if(opProduit.isEmpty()) {
            throw new IllegalArgumentException("Produit not found");
        }
            Produit produit = opProduit.get();

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
