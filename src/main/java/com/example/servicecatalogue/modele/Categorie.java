package com.example.servicecatalogue.modele;


import com.example.servicecatalogue.dtos.CategorieDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "CATEGORIE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_categorie;

    @Setter
    private String libelle;

    @Setter
    @OneToMany(mappedBy = "category")
    private List<Produit> produit;

    @Override
    public String toString() {
        return "Categorie{" +
                "id_categorie=" + id_categorie +
                ", libelle='" + libelle + '\'' +
                ", produit=" + produit +
                '}';
    }

    public static CategorieDTO toDTO(Categorie categorie, int nb_produits) {
        return new CategorieDTO(
                categorie.getId_categorie(),
                categorie.getLibelle(),
                nb_produits
        );
    }
}
