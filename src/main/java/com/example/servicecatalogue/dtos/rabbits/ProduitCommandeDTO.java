package com.example.servicecatalogue.dtos.rabbits;

import java.io.Serializable;

public record ProduitCommandeDTO(int id_produit, String couleur, int quantite) implements Serializable {
}
