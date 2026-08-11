package com.acessibiliadade.pop.enums;

// Fonte única dos idiomas suportados — GET /api/accessibility/languages,
// a validação de preferredLanguage e o default de UserAccessibilitySettings
// leem todos daqui, pra não correrem o risco de ficar dessincronizados.
// (Nome "Code" pra não colidir com o record AccessibilityDTOs.SupportedLanguage,
// que é a forma serializada — code + nome de exibição — devolvida pela API.)
public enum SupportedLanguageCode {
    PT_BR("pt-BR", "Português (Brasil)"),
    EN_US("en-US", "English (US)"),
    ES_ES("es-ES", "Español"),
    FR_FR("fr-FR", "Français"),
    DE_DE("de-DE", "Deutsch");

    public static final SupportedLanguageCode DEFAULT = PT_BR;

    private final String code;
    private final String displayName;

    SupportedLanguageCode(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static boolean isSupported(String code) {
        for (SupportedLanguageCode language : values()) {
            if (language.code.equals(code)) return true;
        }
        return false;
    }
}
