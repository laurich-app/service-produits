package com.example.servicecatalogue.dtos;

import com.example.servicecatalogue.enums.Sexe;
import com.example.servicecatalogue.enums.Taille;

import java.util.List;


public record ProduitDTO (List<String> couleurs, String libelle, String description, double prix_unitaire, String image_url, int categorie_id, Sexe sexe, Taille taille) {
}
