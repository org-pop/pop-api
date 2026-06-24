package com.acessibiliadade.pop.service;

import com.acessibiliadade.pop.exception.BusinessException;
import com.acessibiliadade.pop.exception.DuplicateResourceException;
import com.acessibiliadade.pop.exception.ResourceNotFoundException;

/**
 * EXEMPLOS de como usar as exceções customizadas nos seus Services.
 *
 * Copie os padrões abaixo para ProductService, OrderService, etc.
 * O GlobalExceptionHandler converte automaticamente para JSON.
 */
public class ExceptionUsageExamples {

    // ---- Em ProductService ----

    void findProductExample(Long id, Object productRepository) {
        // ANTES (provavelmente retornava null ou Optional vazio sem tratar):
        // Product product = productRepository.findById(id).orElse(null);

        // DEPOIS — lança 404 automaticamente via GlobalExceptionHandler:
        // Product product = productRepository.findById(id)
        //     .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
    }

    void checkStockExample(int requested, int available) {
        // Em OrderService, antes de criar o pedido:
        if (requested > available) {
            throw new BusinessException(
                    "Estoque insuficiente. Disponível: " + available + ", solicitado: " + requested
            );
        }
    }

    void checkDuplicateEmailExample(String email, boolean exists) {
        // Em UserService ou AuthService, no registro:
        if (exists) {
            throw new DuplicateResourceException("Email já cadastrado: " + email);
        }
    }

    void cancelOrderExample(String currentStatus) {
        // Em OrderService, ao cancelar:
        if ("DELIVERED".equals(currentStatus) || "SHIPPED".equals(currentStatus)) {
            throw new BusinessException(
                    "Pedido com status '" + currentStatus + "' não pode ser cancelado."
            );
        }
    }
}
