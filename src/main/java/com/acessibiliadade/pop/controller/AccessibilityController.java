package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.dto.AccessibilityDTOs.*;
import com.acessibiliadade.pop.security.AuthorizationService;
import com.acessibiliadade.pop.service.AccessibilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accessibility")
@RequiredArgsConstructor
public class AccessibilityController {

    private final AccessibilityService accessibilityService;
    private final AuthorizationService authorizationService;

    @GetMapping("/languages")
    public ResponseEntity<SupportedLanguagesResponse> getSupportedLanguages() {
        return ResponseEntity.ok(accessibilityService.getSupportedLanguages());
    }

    @GetMapping("/users/{userId}/settings")
    public ResponseEntity<AccessibilitySettingsResponse> getSettings(@PathVariable UUID userId) {
        authorizationService.assertOwnership(userId);
        return ResponseEntity.ok(accessibilityService.getSettings(userId));
    }

    @PutMapping("/users/{userId}/settings")
    public ResponseEntity<AccessibilitySettingsResponse> saveSettings(
            @PathVariable UUID userId,
            @Valid @RequestBody AccessibilitySettingsRequest request) {
        authorizationService.assertOwnership(userId);
        return ResponseEntity.ok(accessibilityService.saveSettings(userId, request));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<AccessibleProductResponse> getAccessibleProduct(
            @PathVariable Long productId) {
        UUID userId = authorizationService.currentUser().getId();
        return ResponseEntity.ok(accessibilityService.getAccessibleProduct(productId, userId));
    }
}