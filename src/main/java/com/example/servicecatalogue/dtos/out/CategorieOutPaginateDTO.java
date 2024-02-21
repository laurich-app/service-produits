package com.example.servicecatalogue.dtos.out;

import com.example.servicecatalogue.modele.Produit;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategorieOutPaginateDTO {
    private int id;
    private String libelle;
    List<Produit> produits;

    public CategorieOutPaginateDTO(int idCategorie, String libelle, List<Produit> produits) {
        this.id=idCategorie;
        this.libelle=libelle;
        this.produits=produits;

    }

}
