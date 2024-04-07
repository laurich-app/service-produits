package com.example.servicecatalogue.dtos.rabbits;

import java.io.Serializable;

public record ProduitCatalogueDTO(int id_produit, double prix, String sexe, String taille, String image_url, String couleur, int quantite, String libelle, String description, CategorieCatalogueDTO categorie) implements Serializable {
}
