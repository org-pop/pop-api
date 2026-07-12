package com.acessibiliadade.pop.dto;

import com.acessibiliadade.pop.enums.AccessibilityProfile;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class AccessibilityDTOs {

    public record AccessibilitySettingsRequest(
            Set<AccessibilityProfile> profiles,

            @Pattern(regexp = "pt-BR|en-US|es-ES|fr-FR|de-DE",
                    message = "Idioma não suportado. Use: pt-BR, en-US, es-ES, fr-FR ou de-DE")
            String preferredLanguage,

            Boolean simplifiedLanguage,
            Boolean screenReaderMode,

            @Pattern(regexp = "normal|large|extra-large",
                    message = "Tamanho inválido. Use: normal, large ou extra-large")
            String fontSizePreference,

            @Pattern(regexp = "default|high-contrast|dark",
                    message = "Tema inválido. Use: default, high-contrast ou dark")
            String colorTheme
    ) {}

    public record AccessibilitySettingsResponse(
            Set<AccessibilityProfile> profiles,
            String preferredLanguage,
            Boolean simplifiedLanguage,
            Boolean screenReaderMode,
            String fontSizePreference,
            String colorTheme
    ) {}

    public record AccessibleProductResponse(
            Long id,
            String name,
            String franchise,
            String rarity,
            BigDecimal price,
            Integer stockQuantity,
            String description,
            String accessibleDescription,
            String imageAltText,
            String imageUrl,
            String colorPalette,
            Boolean highContrast,
            String translatedDescription,
            String language
    ) {}

    public record SupportedLanguage(String code, String name) {}

    public record SupportedLanguagesResponse(List<SupportedLanguage> languages) {}
}
