package com.example.servicecatalogue.dtos.out;

import com.example.servicecatalogue.enums.Sexe;
import com.example.servicecatalogue.enums.Taille;

import java.util.List;


public record ProduitOutPaginateDTO(
        Integer id,
        String libelle,
        String description,
        String image_url,
        Double prix_unitaire,
        Sexe sexe,
        Taille taille,
        List<StocksOutDTO> stock
) {
}
