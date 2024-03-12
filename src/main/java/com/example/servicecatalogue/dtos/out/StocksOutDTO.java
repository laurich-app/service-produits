package com.example.servicecatalogue.dtos.out;

import com.example.servicecatalogue.enums.Couleurs;
import com.example.servicecatalogue.modele.Stocks;

public record StocksOutDTO(Couleurs couleur, int quantite) {
    public static StocksOutDTO fromStock(Stocks stock) {
        return new StocksOutDTO(stock.getCouleurs(), stock.getQuantite());
    }
}
