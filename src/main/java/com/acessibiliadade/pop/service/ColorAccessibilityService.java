package com.acessibiliadade.pop.service;

import com.acessibiliadade.pop.enums.AccessibilityProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Serviço de acessibilidade de cores.
 *
 * Usa duas APIs públicas gratuitas:
 *
 * 1. The Color API — https://www.thecolorapi.com
 *    Retorna nome da cor, cores complementares, esquemas.
 *    Sem autenticação necessária.
 *    Ex: GET https://www.thecolorapi.com/id?hex=FFD700
 *
 * 2. Colour Contrast Checker (WCAG) — https://webaim.org/resources/contrastchecker/
 *    Verifica se um par de cores passa nos critérios WCAG AA/AAA.
 *    Ex: GET https://webaim.org/resources/contrastchecker/?fcolor=000000&bcolor=FFD700&api
 */
@Service
public class ColorAccessibilityService {

    private static final Logger log = LoggerFactory.getLogger(ColorAccessibilityService.class);
    private static final String COLOR_API_URL = "https://www.thecolorapi.com/id?hex=";
    private static final String CONTRAST_API_URL = "https://webaim.org/resources/contrastchecker/";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Dado um hex de cor, retorna o nome legível da cor em inglês.
     * Útil para descrever cores para usuários com daltonismo ou cegos.
     *
     * Ex: "FFD700" → "Golden Yellow"
     */
    public String getColorName(String hex) {
        String cleanHex = hex.replace("#", "");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(COLOR_API_URL + cleanHex))
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                return json.path("name").path("value").asText(hex);
            }
        } catch (Exception e) {
            log.error("Erro ao buscar nome da cor {}: {}", hex, e.getMessage());
        }
        return hex;
    }

    /**
     * Verifica se um par de cores passa no critério WCAG AA (nível mínimo recomendado).
     * Ratio mínimo: 4.5:1 para texto normal, 3:1 para texto grande.
     *
     * @param foregroundHex Cor do texto (ex: "000000")
     * @param backgroundHex Cor do fundo (ex: "FFFFFF")
     * @return true se passa em WCAG AA
     */
    public boolean passesWcagAA(String foregroundHex, String backgroundHex) {
        String fg = foregroundHex.replace("#", "");
        String bg = backgroundHex.replace("#", "");
        try {
            String url = CONTRAST_API_URL + "?fcolor=" + fg + "&bcolor=" + bg + "&api";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                return "pass".equalsIgnoreCase(json.path("AA").asText("fail"));
            }
        } catch (Exception e) {
            log.error("Erro ao verificar contraste WCAG: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Retorna nomes legíveis das cores de um produto a partir da paleta HEX.
     * Útil para descrever produtos para usuários com daltonismo.
     *
     * @param colorPalette String com HEX separados por vírgula: "#FF0000,#00FF00,#0000FF"
     * @return Lista de nomes: ["Red", "Green", "Blue"]
     */
    public List<String> describeColorPalette(String colorPalette) {
        List<String> names = new ArrayList<>();
        if (colorPalette == null || colorPalette.isBlank()) return names;

        for (String hex : colorPalette.split(",")) {
            String trimmed = hex.trim();
            if (!trimmed.isEmpty()) {
                names.add(getColorName(trimmed));
            }
        }
        return names;
    }

    /**
     * Verifica se um produto é adequado para um usuário com determinado perfil de daltonismo.
     * Retorna true se as cores do produto são distinguíveis para aquele tipo de daltonismo.
     *
     * Lógica simplificada baseada nos tipos de daltonismo:
     * - Deuteranopia/Protanopia: dificuldade com vermelho-verde
     * - Tritanopia: dificuldade com azul-amarelo
     */
    public boolean isSuitableForColorBlindness(String colorPalette,
                                               Set<AccessibilityProfile> profiles) {
        if (colorPalette == null || colorPalette.isBlank()) return true;

        boolean hasRedGreenBlindness = profiles.contains(AccessibilityProfile.COLOR_BLINDNESS_RED_GREEN);
        boolean hasBlueYellowBlindness = profiles.contains(AccessibilityProfile.COLOR_BLINDNESS_BLUE);

        // Verifica se o produto depende muito de vermelho/verde para usuários com deuteranopia
        if (hasRedGreenBlindness) {
            for (String hex : colorPalette.split(",")) {
                String h = hex.trim().replace("#", "");
                if (h.length() == 6) {
                    int r = Integer.parseInt(h.substring(0, 2), 16);
                    int g = Integer.parseInt(h.substring(2, 4), 16);
                    int b = Integer.parseInt(h.substring(4, 6), 16);
                    // Se vermelho ou verde dominante sem azul como diferenciador, pode ser confuso
                    if (Math.abs(r - g) > 100 && b < 50) return false;
                }
            }
        }

        return true; // Produto adequado
    }
}
