package com.example.servicecatalogue.dtos;

import com.example.servicecatalogue.enums.Couleurs;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ProduitDTO {

    private List<String> couleurs;
    private String libelle;
    private String description;
    private double prix_unitaire;
    private String image_url;
    private int categorie_id;
    private String sexe;
    private String taille;
}
