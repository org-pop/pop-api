package com.acessibiliadade.pop.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductDTOs {

    public record ProductRequest(
            @NotBlank(message = "Nome do produto é obrigatório")
            @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
            String name,

            @NotBlank(message = "Franquia é obrigatória")
            String franchise,

            @NotBlank(message = "Raridade é obrigatória")
            String rarity,

            @NotNull(message = "Preço é obrigatório")
            @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
            BigDecimal price,

            @NotNull(message = "Quantidade em estoque é obrigatória")
            @Min(value = 0, message = "Estoque não pode ser negativo")
            Integer stockQuantity,

            String description,
            String imageUrl
    ) {}

    public record ProductResponse(
            Long id,
            String name,
            String franchise,
            String rarity,
            BigDecimal price,
            Integer stockQuantity,
            String description,
            String imageUrl
    ) {}
}
