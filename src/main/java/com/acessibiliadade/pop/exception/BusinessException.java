package com.acessibiliadade.pop.exception;

/**
 * Lançar quando uma regra de negócio for violada.
 * Exemplo: estoque insuficiente, pedido já cancelado, etc.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
