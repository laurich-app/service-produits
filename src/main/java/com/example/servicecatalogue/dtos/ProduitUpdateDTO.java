package com.example.servicecatalogue.dtos;

import com.example.servicecatalogue.enums.Sexe;
import com.example.servicecatalogue.enums.Taille;

public record ProduitUpdateDTO(String libelle, String description, double prix_unitaire, String image_url, int categorie_id, Sexe sexe, Taille taille) {
}
