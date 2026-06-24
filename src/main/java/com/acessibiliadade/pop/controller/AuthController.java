package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.dto.AuthDTOs.AuthResponse;
import com.acessibiliadade.pop.dto.AuthDTOs.LoginRequest;
import com.acessibiliadade.pop.dto.AuthDTOs.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * EXEMPLO de como aplicar @Valid no controller.
 *
 * A anotação @Valid no parâmetro do método ativa as validações
 * declaradas no DTO (AuthDTOs.java). Se algum campo falhar,
 * o GlobalExceptionHandler intercepta e retorna 422 com detalhes.
 *
 * SUBSTITUA seu AuthController atual por este padrão.
 * Os métodos internos (authService.register, authService.login)
 * permanecem os mesmos que você já tem.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Injete seu AuthService aqui normalmente
    // private final AuthService authService;

    /**
     * Registro — valida name, email (formato) e password (min 6 chars)
     * antes de chegar no service.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // return ResponseEntity.status(201).body(authService.register(request));
        throw new UnsupportedOperationException("Implemente chamando seu AuthService");
    }

    /**
     * Login — valida email e senha não vazios antes de autenticar.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // return ResponseEntity.ok(authService.login(request));
        throw new UnsupportedOperationException("Implemente chamando seu AuthService");
    }
}
