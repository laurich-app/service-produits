package com.example.servicecatalogue.dtos.rabbits;


import java.io.Serializable;

public record SupprimerStockDTO(String couleur, int id_produit) implements Serializable {
}
