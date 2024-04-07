package com.example.servicecatalogue.modele;

import com.example.servicecatalogue.enums.Couleurs;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "STOCKS")
@NoArgsConstructor
@AllArgsConstructor
public class Stocks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Column(name = "id_stock")
    private int idStock;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "id_produit")
    private Produit produit;

    @Getter
    @Setter
    private Couleurs couleurs;

    @Getter @Setter
    private int quantite;
}
