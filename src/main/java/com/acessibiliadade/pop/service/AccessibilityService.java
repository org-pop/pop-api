package com.acessibiliadade.pop.service;

import com.acessibiliadade.pop.dto.AccessibilityDTOs.*;
import com.acessibiliadade.pop.enums.AccessibilityProfile;
import com.acessibiliadade.pop.exception.ResourceNotFoundException;
import com.acessibiliadade.pop.model.Product;
import com.acessibiliadade.pop.model.User;
import com.acessibiliadade.pop.model.UserAccessibilitySettings;
import com.acessibiliadade.pop.repository.ProductRepository;
import com.acessibiliadade.pop.repository.UserAccessibilitySettingsRepository;
import com.acessibiliadade.pop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccessibilityService {

    private final UserAccessibilitySettingsRepository settingsRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TranslationService translationService;
    private final ColorAccessibilityService colorService;

    // PERFIL DO USUÁRIO
    /**
     * Salva ou atualiza as preferências de acessibilidade do usuário.
     */
    @Transactional
    public AccessibilitySettingsResponse saveSettings(UUID userId,
                                                      AccessibilitySettingsRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + userId));

        UserAccessibilitySettings settings = settingsRepository
                .findByUserId(userId)
                .orElse(new UserAccessibilitySettings());

        settings.setUser(user);
        if (request.profiles() != null)          settings.setProfiles(request.profiles());
        if (request.preferredLanguage() != null) settings.setPreferredLanguage(request.preferredLanguage());
        if (request.simplifiedLanguage() != null) settings.setSimplifiedLanguage(request.simplifiedLanguage());
        if (request.screenReaderMode() != null)  settings.setScreenReaderMode(request.screenReaderMode());
        if (request.fontSizePreference() != null) settings.setFontSizePreference(request.fontSizePreference());
        if (request.colorTheme() != null)         settings.setColorTheme(request.colorTheme());

        settingsRepository.save(settings);
        return toResponse(settings);
    }

    /**
     * Busca as preferências de acessibilidade de um usuário.
     */
    public AccessibilitySettingsResponse getSettings(UUID userId) {
        UserAccessibilitySettings settings = settingsRepository
                .findByUserId(userId)
                .orElseGet(() -> {
                    // Retorna configuração padrão se não existir
                    UserAccessibilitySettings def = new UserAccessibilitySettings();
                    def.setProfiles(Set.of(AccessibilityProfile.NONE));
                    return def;
                });
        return toResponse(settings);
    }

    // PRODUTO ACESSÍVEL
    /**
     * Retorna um produto com todos os dados de acessibilidade:
     * - Descrição detalhada para leitores de tela
     * - Nomes das cores (para daltonismo)
     * - Descrição traduzida para o idioma preferido do usuário
     */
    public AccessibleProductResponse getAccessibleProduct(Long productId, UUID userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + productId));

        // Busca configurações do usuário (pode ser null se não tiver conta)
        UserAccessibilitySettings settings = userId != null
                ? settingsRepository.findByUserId(userId).orElse(null)
                : null;

        String lang = settings != null ? settings.getPreferredLanguage() : "pt-BR";
        boolean screenReader = settings != null && Boolean.TRUE.equals(settings.getScreenReaderMode());

        // Traduz a descrição se o idioma preferido não for pt-BR
        String translatedDescription = null;
        if (!"pt-BR".equals(lang) && product.getDescription() != null) {
            translatedDescription = translationService.translate(
                    product.getDescription(), "pt", lang);
        }

        // Descreve as cores em linguagem natural (para daltonismo / leitores de tela)
        List<String> colorNames = colorService.describeColorPalette(product.getColorPalette());
        String colorDescription = colorNames.isEmpty() ? null : String.join(", ", colorNames);

        // Para leitores de tela: enriquece a descrição acessível com nomes das cores
        String accessibleDesc = product.getAccessibleDescription();
        if (screenReader && colorDescription != null && accessibleDesc != null) {
            accessibleDesc = accessibleDesc + " Cores predominantes: " + colorDescription + ".";
        }

        return new AccessibleProductResponse(
                product.getId(),
                product.getName(),
                product.getFranchise(),
                product.getRarity(),
                product.getPrice(),
                product.getStock(),
                product.getDescription(),
                accessibleDesc,
                product.getImageAltText(),
                product.getImageUrl(),
                product.getColorPalette(),
                product.getHighContrast(),
                translatedDescription,
                lang
        );
    }

    // FILTRO DE PRODUTOS POR ACESSIBILIDADE
    /**
     * Filtra produtos da lista de acordo com os perfis de acessibilidade do usuário.
     * Produtos inadequados para o perfil são removidos ou marcados.
     */
    public List<Product> filterProductsByAccessibility(List<Product> products, UUID userId) {
        if (userId == null) return products;

        UserAccessibilitySettings settings = settingsRepository
                .findByUserId(userId).orElse(null);

        if (settings == null || settings.getProfiles().contains(AccessibilityProfile.NONE)) {
            return products;
        }

        Set<AccessibilityProfile> profiles = settings.getProfiles();

        return products.stream()
                .filter(p -> colorService.isSuitableForColorBlindness(p.getColorPalette(), profiles))
                .toList();
    }

    // IDIOMAS SUPORTADOS
    public SupportedLanguagesResponse getSupportedLanguages() {
        List<SupportedLanguage> languages = List.of(
                new SupportedLanguage("pt-BR", "Português (Brasil)"),
                new SupportedLanguage("en-US", "English (US)"),
                new SupportedLanguage("es-ES", "Español"),
                new SupportedLanguage("fr-FR", "Français"),
                new SupportedLanguage("de-DE", "Deutsch")
        );
        return new SupportedLanguagesResponse(languages);
    }

    // HELPERS
    private AccessibilitySettingsResponse toResponse(UserAccessibilitySettings s) {
        return new AccessibilitySettingsResponse(
                s.getProfiles(),
                s.getPreferredLanguage(),
                s.getSimplifiedLanguage(),
                s.getScreenReaderMode(),
                s.getFontSizePreference(),
                s.getColorTheme()
        );
    }
}