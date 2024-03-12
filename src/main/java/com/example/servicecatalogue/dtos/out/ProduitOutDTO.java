package com.example.servicecatalogue.dtos.out;

import com.example.servicecatalogue.enums.Sexe;
import com.example.servicecatalogue.enums.Taille;
import com.example.servicecatalogue.modele.Produit;
import com.example.servicecatalogue.modele.Stocks;

import java.util.ArrayList;
import java.util.List;

public record ProduitOutDTO(List<StocksOutDTO> stock, int id, double prix_unitaire, Sexe sexe, Taille taille, String image_url, String description, String libelle, CategorieOutDTO categorie) {

    public static ProduitOutDTO fromProduit(Produit produit) {
        List<StocksOutDTO> stocks = new ArrayList<>();

        for(Stocks stock : produit.getStocks()) {
            stocks.add(StocksOutDTO.fromStock(stock));
        }

        return new ProduitOutDTO(
                stocks,
                produit.getId(),
                produit.getPrix_unitaire(),
                produit.getSexe(),
                produit.getTaille(),
                produit.getImage(),
                produit.getDescription(),
                produit.getLibelle(),
                new CategorieOutDTO(produit.getCategory().getId_categorie(), produit.getCategory().getLibelle())
        );
    }
}
