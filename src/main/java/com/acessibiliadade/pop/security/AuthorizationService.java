package com.acessibiliadade.pop.security;

import com.acessibiliadade.pop.exception.ResourceNotFoundException;
import com.acessibiliadade.pop.model.User;
import com.acessibiliadade.pop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;

    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetails details)) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        return userRepository.findByEmail(details.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
    }

    public void assertOwnership(UUID userIdFromPath) {
        UUID current = currentUser().getId();
        if (!current.equals(userIdFromPath)) {
            throw new AccessDeniedException("Você não tem permissão para acessar recursos de outro usuário");
        }
    }
}
