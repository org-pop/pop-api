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

@Service
public class ColorAccessibilityService {

    private static final Logger log = LoggerFactory.getLogger(ColorAccessibilityService.class);
    private static final String COLOR_API_URL = "https://www.thecolorapi.com/id?hex=";
    private static final String CONTRAST_API_URL = "https://webaim.org/resources/contrastchecker/";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public boolean isSuitableForColorBlindness(String colorPalette,
                                               Set<AccessibilityProfile> profiles) {
        if (colorPalette == null || colorPalette.isBlank()) return true;

        boolean hasRedGreenBlindness = profiles.contains(AccessibilityProfile.COLOR_BLINDNESS_RED_GREEN);
        boolean hasBlueYellowBlindness = profiles.contains(AccessibilityProfile.COLOR_BLINDNESS_BLUE);

        if (hasRedGreenBlindness) {
            for (String hex : colorPalette.split(",")) {
                String h = hex.trim().replace("#", "");
                if (h.length() == 6) {
                    int r = Integer.parseInt(h.substring(0, 2), 16);
                    int g = Integer.parseInt(h.substring(2, 4), 16);
                    int b = Integer.parseInt(h.substring(4, 6), 16);
                    if (Math.abs(r - g) > 100 && b < 50) return false;
                }
            }
        }

        return true;
    }
}
