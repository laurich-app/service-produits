package com.example.servicecatalogue.dtos;

import lombok.Getter;
import lombok.Setter;

    @Getter
    @Setter
    public class CategorieDTO {

        private int id;
        private String libelle;
        private int nb_produits;

        public CategorieDTO(int idCategorie, String libelle, int nbProduits) {
            this.id=idCategorie;
            this.libelle=libelle;
            this.nb_produits=nbProduits;
        }
    }

