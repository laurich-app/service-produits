package com.example.servicecatalogue.dtos;

import lombok.Getter;
import lombok.Setter;

    @Getter
    @Setter
    public class CategorieDTO {

        private int id;
        private String libelle;
        private int nbProduits;

        public CategorieDTO(int idCategorie, String libelle, int nbProduits) {
            this.id=idCategorie;
            this.libelle=libelle;
            this.nbProduits =nbProduits;
        }
    }

