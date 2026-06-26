package com.acessibiliadade.pop.exception;

/**
 * Lançar quando as credenciais de login forem inválidas.
 * Exemplo: senha incorreta, email não encontrado no login.
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}