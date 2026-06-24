package com.acessibiliadade.pop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "franchise", length = 100)
    private String franchise;

    @Column(name = "rarity", length = 50)
    private String rarity;

    /**
     * Descrição detalhada para leitores de tela.
     * Gerada via LibreTranslate ou preenchida manualmente.
     * Ex: "Funko Pop do Batman da série DC, 9cm, acabamento fosco,
     *      capa azul com símbolo do morcego em dourado, base preta."
     */
    @Column(name = "accessible_description", columnDefinition = "TEXT")
    private String accessibleDescription; // NOVO

    /**
     * Texto alternativo curto para a imagem (equivalente ao alt="" do HTML).
     * Usado por leitores de tela ao descrever a imagem do produto.
     */
    @Column(name = "image_alt_text", length = 255)
    private String imageAltText; // NOVO

    /**
     * Paleta de cores predominantes do produto em HEX, separadas por vírgula.
     * Usada para filtrar produtos por usuários com daltonismo.
     * Ex: "#1A1A2E,#FFD700,#1C1C1C"
     */
    @Column(name = "color_palette", length = 100)
    private String colorPalette; // NOVO

    /**
     * Flag para indicar se este produto tem alto contraste visual
     * (útil para usuários com baixa visão).
     */
    @Column(name = "high_contrast")
    private Boolean highContrast = false; // NOVO
}