package com.acessibiliadade.pop.enums;

public enum AccessibilityProfile {
    VISUAL_IMPAIRMENT("Deficiência visual (leitor de tela)"),
    LOW_VISION("Baixa visão (alto contraste, texto ampliado)"),
    COLOR_BLINDNESS_RED_GREEN("Daltonismo vermelho-verde"),
    COLOR_BLINDNESS_BLUE("Daltonismo azul-amarelo"),
    COLOR_BLINDNESS_FULL("Daltonismo total (escala de cinza)"),
    MOTOR_IMPAIRMENT("Dificuldade motora"),
    COGNITIVE_IMPAIRMENT("Dificuldade cognitiva"),
    DYSLEXIA("Dislexia"),
    HEARING_IMPAIRMENT("Deficiência auditiva"),
    NONE("Nenhuma necessidade especial");

    private final String description;

    AccessibilityProfile(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}