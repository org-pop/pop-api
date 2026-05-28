package com.acessibiliadade.pop.enums;

/**
 * Status do pagamento
 */
public enum PaymentStatus {

    PENDING("Aguardando pagamento"),
    PROCESSING("Processando"),
    APPROVED("Aprovado"),
    DECLINED("Recusado"),
    REFUNDED("Estornado"),
    CANCELLED("Cancelado");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}