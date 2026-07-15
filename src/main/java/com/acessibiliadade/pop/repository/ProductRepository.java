package com.acessibiliadade.pop.repository;

import com.acessibiliadade.pop.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    List<Product> findByFranchise(String franchise);

    List<Product> findByRarity(String rarity);

    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    List<Product> findByStockLessThan(Integer stock);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByFranchiseIgnoreCase(String franchise);

    List<Product> findByRarityIgnoreCase(String rarity);
}