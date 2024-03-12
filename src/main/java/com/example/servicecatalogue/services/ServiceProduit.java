package com.example.servicecatalogue.services;

import com.example.servicecatalogue.dtos.ProduitDTO;
import com.example.servicecatalogue.dtos.ProduitUpdateDTO;
import com.example.servicecatalogue.dtos.out.ProduitOutPaginateDTO;
import com.example.servicecatalogue.dtos.pagination.Paginate;
import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.dtos.pagination.Pagination;
import com.example.servicecatalogue.enums.Couleurs;
import com.example.servicecatalogue.enums.Sexe;
import com.example.servicecatalogue.enums.Taille;
import com.example.servicecatalogue.exceptions.StockExisteDejaException;
import com.example.servicecatalogue.modele.Categorie;
import com.example.servicecatalogue.modele.Produit;
import com.example.servicecatalogue.modele.Stocks;
import com.example.servicecatalogue.repositories.CategorieRepository;
import com.example.servicecatalogue.repositories.StocksRepository;
import com.example.servicecatalogue.utils.PageableUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.servicecatalogue.repositories.ProduitRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceProduit {

    @Autowired
    private final ProduitRepository produitRepository;
    @Autowired
    private final CategorieRepository categorieRepository;
    @Autowired
    private final StocksRepository stockRepository;

    public ServiceProduit(ProduitRepository produitRepository, CategorieRepository categorieRepository, StocksRepository stockRepository) {
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
        this.stockRepository = stockRepository;
    }

    @Transactional
    public Produit saveProduit(ProduitDTO produitDTO) {
        if (produitDTO.getPrix_unitaire() <= 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être positif");
        }
        Categorie categorie = categorieRepository.findById(produitDTO.getCategorie_id())
                .orElseThrow(() -> new EntityNotFoundException("La catégorie spécifiée n'existe pas"));

        if (produitDTO.getSexe() == null || produitDTO.getTaille() == null || produitDTO.getLibelle() == null) {
            throw new IllegalArgumentException("Les champs 'sexe', 'taille' et 'libelle' ne doivent pas être nuls");
        }
        String libelleMajuscules = produitDTO.getLibelle().toUpperCase();

        Produit produit = new Produit();
        produit.setPrix_unitaire(produitDTO.getPrix_unitaire());
        produit.setSexe(Sexe.valueOf(produitDTO.getSexe()));
        produit.setTaille(Taille.valueOf(produitDTO.getTaille()));
        produit.setLibelle(libelleMajuscules);
        produit.setDescription(produitDTO.getDescription());
        produit.setImage(produitDTO.getImage_url());
        produit.setCategory(categorie);

        produitRepository.save(produit);

        if (produitDTO.getCouleurs() == null || produitDTO.getCouleurs().isEmpty()) {
            throw new IllegalArgumentException("La liste des couleurs ne doit pas être nulle ou vide");
        }

        List<Stocks> stocks = new ArrayList<>();
        for (String couleur : produitDTO.getCouleurs()) {
            if (couleur == null || Couleurs.valueOf(couleur) == null) {
                throw new IllegalArgumentException("La couleur '" + couleur + "' n'est pas valide");
            }
            Stocks stock = new Stocks();
            stock.setQuantite(0);
            stock.setCouleurs(Couleurs.valueOf(couleur));
            stock.setProduit(produit);
            stockRepository.save(stock);
            stocks.add(stock);
        }

        produit.setStocks(stocks);
        return produit;
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

    @Transactional
    public String deleteProduit(int id) {
        if (!produitRepository.existsById(id)) {
            throw new EntityNotFoundException("Produit non trouvé pour l'ID :" +id);
        }
        produitRepository.deleteById(id);
        return "Produit supprimé !";
    }

    @Transactional
    public Produit updateProduit(int id , ProduitUpdateDTO produitDTO) {
        Optional<Produit> opProduit = produitRepository.findById(id);
        if(opProduit.isEmpty()) {
            throw new EntityNotFoundException("Produit not found");
        }
        Produit produit = opProduit.get();

        if(produitDTO.prix_unitaire() >= 0f)
            produit.setPrix_unitaire(produitDTO.prix_unitaire());

        if(produitDTO.sexe() != null)
            produit.setSexe(produitDTO.sexe());

        if(produitDTO.taille() != null)
            produit.setTaille(produitDTO.taille());

        if(produitDTO.libelle() != null)
            produit.setLibelle(produitDTO.libelle());

        if(produitDTO.image_url() != null)
            produit.setImage(produitDTO.image_url());

        if(produitDTO.description() != null)
            produit.setDescription(produitDTO.description());

        if(produitDTO.categorie_id() != 0) {
            Optional<Categorie> categorie = categorieRepository.findById(produitDTO.categorie_id());
            if(categorie.isEmpty()) {
                throw new EntityNotFoundException("Categorie not found");
            }
            produit.setCategory(categorie.get());
        }
        return produitRepository.save(produit);
    }

    public Stocks addStock(int id, Couleurs couleur) throws StockExisteDejaException {
        Optional<Produit> opProduit = produitRepository.findById(id);
        if(opProduit.isEmpty()) {
            throw new EntityNotFoundException("Produit not found");
        }
        Produit produit = opProduit.get();

        Stocks stock = stockRepository.findByProduitIdAndCouleurs(id, couleur);
        if(stock != null) {
            throw new StockExisteDejaException("Le stock existe déjà");
        }

        Stocks s = new Stocks();
        s.setQuantite(0);
        s.setProduit(produit);
        s.setCouleurs(couleur);
        return stockRepository.save(s);
    }

    public void deleteStock(int id, Couleurs couleur) {
        Stocks stock = stockRepository.findByProduitIdAndCouleurs(id, couleur);
        if(stock == null) {
            throw new EntityNotFoundException("Le stock existe déjà");
        }

        stockRepository.delete(stock);
    }
}
