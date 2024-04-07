package com.example.servicecatalogue.modele;


import com.example.servicecatalogue.dtos.out.ProduitOutPaginateDTO;
import com.example.servicecatalogue.dtos.out.StocksOutDTO;
import com.example.servicecatalogue.dtos.rabbits.CategorieCatalogueDTO;
import com.example.servicecatalogue.dtos.rabbits.ProduitCatalogueDTO;
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
    private double prixUnitaire;

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
                produit.getPrixUnitaire(),
                produit.getSexe(),
                produit.getTaille(),
                produit.getStocks().stream().map(StocksOutDTO::fromStock).collect(Collectors.toList())
        );
    }

    /**
     * Ligne 4 : génère les infos à envoyer à la commande
     * @param produit
     * @param couleur
     * @param quantite
     * @return
     */
    public static ProduitCatalogueDTO toRabbitMqDTO(Produit produit, String couleur, int quantite) {
        return new ProduitCatalogueDTO(
                produit.getId(),
                produit.getPrixUnitaire(),
                produit.getSexe().toString(),
                produit.getTaille().toString(),
                produit.getImage(),
                couleur,
                quantite,
                produit.getLibelle(),
                produit.getDescription(),
                new CategorieCatalogueDTO(produit.getCategory().getLibelle())
        );
    }
}
