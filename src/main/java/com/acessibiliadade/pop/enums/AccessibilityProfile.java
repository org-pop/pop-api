package com.acessibiliadade.pop.enums;

/**
 * Perfis de acessibilidade disponíveis para o usuário.
 * Um usuário pode ter múltiplos perfis simultaneamente.
 */
public enum AccessibilityProfile {

    /** Deficiência visual (usa leitor de tela) */
    VISUAL_IMPAIRMENT("Deficiência visual (leitor de tela)"),

    /** Baixa visão (necessita de alto contraste e texto ampliado) */
    LOW_VISION("Baixa visão (alto contraste, texto ampliado)"),

    /** Daltonismo vermelho-verde (deuteranopia/protanopia) */
    COLOR_BLINDNESS_RED_GREEN("Daltonismo vermelho-verde"),

    /** Daltonismo azul-amarelo (tritanopia) */
    COLOR_BLINDNESS_BLUE("Daltonismo azul-amarelo"),

    /** Acromatopsia (visão em escala de cinza) */
    COLOR_BLINDNESS_FULL("Daltonismo total (escala de cinza)"),

    /** Dificuldade motora (navegação por teclado/switch) */
    MOTOR_IMPAIRMENT("Dificuldade motora"),

    /** Dificuldade cognitiva (linguagem simples, menos informações) */
    COGNITIVE_IMPAIRMENT("Dificuldade cognitiva"),

    /** Dislexia (fontes e espaçamento adaptados) */
    DYSLEXIA("Dislexia"),

    /** Deficiência auditiva */
    HEARING_IMPAIRMENT("Deficiência auditiva"),

    /** Sem necessidades especiais de acessibilidade */
    NONE("Nenhuma necessidade especial");

    private final String description;

    AccessibilityProfile(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}