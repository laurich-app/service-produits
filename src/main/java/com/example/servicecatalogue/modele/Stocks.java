package com.example.servicecatalogue.modele;

import com.example.servicecatalogue.enums.Couleurs;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "STOCKS")
@NoArgsConstructor
@AllArgsConstructor
public class Stocks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id_stock;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "id_produit", referencedColumnName = "id_produit")
    private Produit id_produit;

    @Getter
    @Setter
    @ElementCollection
    @CollectionTable(name = "COULEURS")
    @Enumerated(EnumType.STRING)
    private List<Couleurs> couleurs;

    @Getter @Setter
    private int quantite;
}
