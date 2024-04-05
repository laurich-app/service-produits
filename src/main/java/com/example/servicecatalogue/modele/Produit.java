package com.example.servicecatalogue.modele;


import com.example.servicecatalogue.dtos.out.ProduitOutPaginateDTO;
import com.example.servicecatalogue.dtos.out.StocksOutDTO;
import com.example.servicecatalogue.enums.Couleurs;
import com.example.servicecatalogue.enums.Sexe;
import com.example.servicecatalogue.enums.Taille;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "PRODUIT")
@NoArgsConstructor
@AllArgsConstructor
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;

    @Getter @Setter
    private double prix_unitaire;

    @Getter @Setter
    private Sexe sexe;

    @Getter @Setter
    private Taille taille;

    @Getter @Setter
    private String libelle;

    @Getter @Setter
    @Column(length = 500)
    private String description;

    @Getter @Setter
    @Column(columnDefinition = "TEXT")
    private String image;

    @Getter @Setter
    @ManyToOne
    private Categorie category;

    @Getter @Setter
    @OneToMany(mappedBy = "produit", cascade = CascadeType.REMOVE)
    private List<Stocks> stocks;

    public static ProduitOutPaginateDTO toDTO(Produit produit) {
        return new ProduitOutPaginateDTO(
                produit.getId(),
                produit.getLibelle(),
                produit.getDescription(),
                produit.getImage(),
                produit.getPrix_unitaire(),
                produit.getSexe(),
                produit.getTaille(),
                produit.getStocks().stream().map(StocksOutDTO::fromStock).collect(Collectors.toList())
        );
    }
}
