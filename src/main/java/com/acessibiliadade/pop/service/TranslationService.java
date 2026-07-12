package com.acessibiliadade.pop.service;

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
import java.util.Map;

@Service
public class TranslationService {

    private static final Logger log = LoggerFactory.getLogger(TranslationService.class);

    private static final String LIBRETRANSLATE_URL = "https://translate.terraprint.co/translate";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String translate(String text, String sourceLang, String targetLang) {
        if (text == null || text.isBlank()) return text;

        String source = normalizeLanguageCode(sourceLang);
        String target = normalizeLanguageCode(targetLang);

        if (source.equals(target)) return text;

        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "q", text,
                    "source", source,
                    "target", target,
                    "format", "text"
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LIBRETRANSLATE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(8))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                return json.path("translatedText").asText(text);
            }

            log.warn("LibreTranslate retornou status {}: {}", response.statusCode(), response.body());
            return text;

        } catch (Exception e) {
            log.error("Erro ao traduzir texto via LibreTranslate: {}", e.getMessage());
            return text;
        }
    }

    public String detectLanguage(String text) {
        if (text == null || text.isBlank()) return "unknown";

        try {
            String body = objectMapper.writeValueAsString(Map.of("q", text));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://translate.terraprint.co/detect"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(5))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                return json.get(0).path("language").asText("unknown");
            }
        } catch (Exception e) {
            log.error("Erro ao detectar idioma: {}", e.getMessage());
        }
        return "unknown";
    }

    private String normalizeLanguageCode(String code) {
        if (code == null) return "pt";
        return code.contains("-") ? code.split("-")[0] : code;
    }
}
