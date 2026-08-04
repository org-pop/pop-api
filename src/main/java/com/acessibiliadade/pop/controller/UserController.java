package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.dto.UserDTOs.UpdateUserRequest;
import com.acessibiliadade.pop.dto.UserDTOs.UserResponse;
import com.acessibiliadade.pop.exception.ResourceNotFoundException;
import com.acessibiliadade.pop.model.User;
import com.acessibiliadade.pop.security.AuthorizationService;
import com.acessibiliadade.pop.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated // habilita as constraints declaradas direto em @PathVariable/@RequestParam
public class UserController {

    private final UserService userService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream().map(UserResponse::from).toList();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        authorizationService.assertOwnership(id);
        return userService.getUserById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    // O próprio usuário sempre pode se buscar; qualquer outro email exige ADMIN.
    // A checagem vem antes da consulta para não virar um enumerador de emails cadastrados.
    @GetMapping("/email/{email}")
    public UserResponse getUserByEmail(
            @PathVariable
            @NotBlank(message = "Email é obrigatório")
            @Email(message = "Email inválido")
            String email) {
        User current = authorizationService.currentUser();
        if (current.getEmail().equalsIgnoreCase(email)) {
            return UserResponse.from(current);
        }
        if (!authorizationService.isAdmin(current)) {
            throw new AccessDeniedException("Você não tem permissão para acessar dados de outro usuário");
        }
        return userService.getUserByEmail(email)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        authorizationService.assertOwnership(id);
        User patch = new User();
        patch.setName(request.name());
        patch.setEmail(request.email());
        patch.setPassword(request.password());
        return UserResponse.from(userService.updateUser(id, patch));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        authorizationService.assertOwnership(id);
        userService.deleteUser(id);
    }
}
