package com.acessibiliadade.pop.repository;

import com.acessibiliadade.pop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByFranchise(String franchise);

    List<Product> findByRarity(String rarity);

    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    List<Product> findByStockLessThan(Integer stock);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByFranchiseIgnoreCase(String franchise);

    List<Product> findByRarityIgnoreCase(String rarity);
}