package com.example.servicecatalogue.repositories;



import com.example.servicecatalogue.modele.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface StocksRepository extends JpaRepository<Stocks, Integer> {
}
