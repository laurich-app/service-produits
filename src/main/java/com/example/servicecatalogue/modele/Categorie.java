package com.example.servicecatalogue.modele;


import com.example.servicecatalogue.dtos.out.CategorieOutPaginateDTO;
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

    public static CategorieOutPaginateDTO toDTO(Categorie categorie) {
        return new CategorieOutPaginateDTO(
                categorie.getId_categorie(),
                categorie.getLibelle(),
                categorie.getProduit()
        );
    }
}
