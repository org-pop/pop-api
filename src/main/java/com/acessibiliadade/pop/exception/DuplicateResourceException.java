package com.acessibiliadade.pop.exception;

/**
 * Lançar quando tentar cadastrar um recurso duplicado.
 * Exemplo: email já cadastrado, SKU de produto repetido, etc.
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
