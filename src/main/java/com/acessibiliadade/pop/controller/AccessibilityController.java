package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.dto.AccessibilityDTOs.*;
import com.acessibiliadade.pop.service.AccessibilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller de acessibilidade.
 *
 * Endpoints disponíveis:
 *
 * GET  /api/accessibility/languages
 *      Lista os idiomas suportados para tradução.
 *
 * GET  /api/accessibility/users/{userId}/settings
 *      Retorna as preferências de acessibilidade do usuário.
 *
 * PUT  /api/accessibility/users/{userId}/settings
 *      Salva/atualiza as preferências de acessibilidade do usuário.
 *
 * GET  /api/accessibility/products/{productId}?userId={userId}
 *      Retorna o produto com dados completos de acessibilidade:
 *      descrição para leitor de tela, nomes das cores, descrição traduzida.
 */
@RestController
@RequestMapping("/api/accessibility")
@RequiredArgsConstructor
public class AccessibilityController {

    private final AccessibilityService accessibilityService;

    // Idiomas suportados
    /**
     * Lista idiomas disponíveis para tradução automática.
     * Não requer autenticação.
     *
     * GET /api/accessibility/languages
     */
    @GetMapping("/languages")
    public ResponseEntity<SupportedLanguagesResponse> getSupportedLanguages() {
        return ResponseEntity.ok(accessibilityService.getSupportedLanguages());
    }

    // Preferências do usuário
    /**
     * Busca as preferências de acessibilidade de um usuário.
     *
     * GET /api/accessibility/users/{userId}/settings
     */
    @GetMapping("/users/{userId}/settings")
    public ResponseEntity<AccessibilitySettingsResponse> getSettings(@PathVariable UUID userId) {
        return ResponseEntity.ok(accessibilityService.getSettings(userId));
    }

    /**
     * Salva ou atualiza as preferências de acessibilidade.
     *
     * PUT /api/accessibility/users/{userId}/settings
     *
     * Body exemplo:
     * {
     *   "profiles": ["VISUAL_IMPAIRMENT", "COLOR_BLINDNESS_RED_GREEN"],
     *   "preferredLanguage": "en-US",
     *   "screenReaderMode": true,
     *   "simplifiedLanguage": false,
     *   "fontSizePreference": "large",
     *   "colorTheme": "high-contrast"
     * }
     */
    @PutMapping("/users/{userId}/settings")
    public ResponseEntity<AccessibilitySettingsResponse> saveSettings(
            @PathVariable UUID userId,
            @Valid @RequestBody AccessibilitySettingsRequest request) {
        return ResponseEntity.ok(accessibilityService.saveSettings(userId, request));
    }

    // Produto acessível
    /**
     * Retorna um produto com dados completos de acessibilidade.
     * Se userId for fornecido, adapta a resposta ao perfil do usuário
     * (traduz para o idioma preferido, enriquece descrição para leitor de tela, etc.)
     *
     * GET /api/accessibility/products/{productId}
     * GET /api/accessibility/products/{productId}?userId=42 (userId é UUID agora)
     *
     * Resposta inclui:
     * - accessibleDescription: texto detalhado para leitores de tela
     * - imageAltText: alt text da imagem
     * - colorPalette: cores em HEX
     * - translatedDescription: descrição no idioma preferido do usuário
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<AccessibleProductResponse> getAccessibleProduct(
            @PathVariable Long productId,
            @RequestParam(required = false) UUID userId) {
        return ResponseEntity.ok(accessibilityService.getAccessibleProduct(productId, userId));
    }
}