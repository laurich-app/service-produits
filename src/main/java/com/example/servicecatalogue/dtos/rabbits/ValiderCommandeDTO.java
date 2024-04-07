package com.example.servicecatalogue.dtos.rabbits;

import java.io.Serializable;
import java.util.List;

public record ValiderCommandeDTO(String id_commande, List<ProduitCommandeDTO> produits) implements Serializable {
    @Override
    public String toString() {
        return "ValiderCommandeDTO{" +
                "id_commande='" + id_commande + '\'' +
                ", produits=" + produits +
                '}';
    }
}
