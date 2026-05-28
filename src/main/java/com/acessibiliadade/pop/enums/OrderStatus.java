package com.acessibiliadade.pop.enums;

/**
 * Status do pedido
 */
public enum OrderStatus {

    /** Pedido criado, aguardando processamento */
    PENDING("Aguardando processamento"),

    /** Pedido em processamento (separação, embalagem) */
    PROCESSING("Em processamento"),

    /** Pedido enviado ao cliente */
    SHIPPED("Enviado"),

    /** Pedido entregue ao cliente */
    DELIVERED("Entregue"),

    /** Pedido cancelado */
    CANCELLED("Cancelado");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}