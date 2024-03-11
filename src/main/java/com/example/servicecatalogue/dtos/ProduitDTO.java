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
    private int stock;
    private String image;
    private int id_categorie;
    private String sexe;
    private String taille;
    private int categorie;
    private int quantite;


}
