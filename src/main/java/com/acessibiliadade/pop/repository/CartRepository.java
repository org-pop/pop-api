package com.acessibiliadade.pop.repository;

import com.acessibiliadade.pop.model.Cart;
import com.acessibiliadade.pop.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(UUID userId);

    // Lock pessimista na linha do carrinho para serializar requisições concorrentes
    // (ex.: duplo clique em "adicionar ao carrinho") que fariam check-then-act sem
    // se enxergar e furariam a validação de estoque em CartService.addItemToCart.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cart c where c.id = :id")
    Optional<Cart> findByIdForUpdate(Long id);
}