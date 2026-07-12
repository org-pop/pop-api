package com.acessibiliadade.pop.enums;

public enum OrderStatus {
    PENDING("Aguardando processamento"),
    PROCESSING("Em processamento"),
    SHIPPED("Enviado"),
    DELIVERED("Entregue"),
    CANCELLED("Cancelado");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}