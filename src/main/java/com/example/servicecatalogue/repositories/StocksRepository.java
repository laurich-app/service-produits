package com.example.servicecatalogue.repositories;



import com.example.servicecatalogue.enums.Couleurs;
import com.example.servicecatalogue.modele.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface StocksRepository extends JpaRepository<Stocks, Integer> {

    @Query("SELECT s FROM Stocks s WHERE s.couleurs = :couleur AND s.produit.id = :id_produit")
    Stocks findByProduitIdAndCouleurs(int id_produit, Couleurs couleur);
}
