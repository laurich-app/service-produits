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
    @Column(name = "id_categorie")
    private int idCategorie;

    @Setter
    private String libelle;

    @Setter
    @OneToMany(mappedBy = "category")
    private List<Produit> produit;

    @Override
    public String toString() {
        return "Categorie{" +
                "id_categorie=" + idCategorie +
                ", libelle='" + libelle + '\'' +
                ", produit=" + produit +
                '}';
    }

    public static CategorieDTO toDTO(Categorie categorie, int nbProduits) {
        return new CategorieDTO(
                categorie.getIdCategorie(),
                categorie.getLibelle(),
                nbProduits
        );
    }
}
