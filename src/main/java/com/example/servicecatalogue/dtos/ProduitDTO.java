package com.example.servicecatalogue.dtos;

import java.util.List;


public record ProduitDTO (List<String> couleurs, String libelle,  String description, double prix_unitaire, String image_url, int categorie_id, String sexe, String taille) {
}
