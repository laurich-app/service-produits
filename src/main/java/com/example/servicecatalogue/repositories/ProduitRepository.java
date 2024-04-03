package com.example.servicecatalogue.repositories;

import com.example.servicecatalogue.modele.Categorie;
import com.example.servicecatalogue.modele.Produit;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    @Query("SELECT p FROM Produit p WHERE p.category.idCategorie = ?1")
    List<Produit> findByCategory(Integer category);

    @Query("SELECT COUNT(p) FROM Produit p WHERE p.category = :category")
    int countByCategory(@Param("category") Categorie category);

    @Modifying
    @Transactional
    @Query("DELETE FROM Produit p WHERE p.category = :category")
    void deleteByCategory(Categorie category);
}
