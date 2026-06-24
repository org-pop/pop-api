package com.acessibiliadade.pop.exception;

/**
 * Lançar quando um recurso não for encontrado no banco.
 * Exemplo: productRepository.findById(id)
 *   .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
