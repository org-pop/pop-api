package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.dto.AuthDTOs.AuthResponse;
import com.acessibiliadade.pop.dto.AuthDTOs.LoginRequest;
import com.acessibiliadade.pop.dto.AuthDTOs.RegisterRequest;
import com.acessibiliadade.pop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registro — valida name, email (formato) e password (min 6 chars)     * antes de chegar no service.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * Login — valida email e senha não vazios antes de autenticar.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}