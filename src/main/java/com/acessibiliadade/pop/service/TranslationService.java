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

/**
 * Serviço de tradução usando a API pública LibreTranslate.
 *
 * API: https://libretranslate.com
 * Docs: https://libretranslate.com/docs
 * Licença: AGPLv3 — pode usar gratuitamente com limite de requisições.
 *
 * Idiomas suportados: pt, en, es, fr, de, it, zh, ja, ar, ru, entre outros.
 *
 * Instância pública gratuita: https://translate.terraprint.co
 * (sem necessidade de API key nessa instância)
 */
@Service
public class TranslationService {

    private static final Logger log = LoggerFactory.getLogger(TranslationService.class);

    // Instância pública do LibreTranslate — sem API key necessária
    private static final String LIBRETRANSLATE_URL = "https://translate.terraprint.co/translate";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Traduz um texto do idioma de origem para o idioma alvo.
     *
     * @param text       Texto a traduzir
     * @param sourceLang Idioma de origem (ex: "pt") — use "auto" para detectar
     * @param targetLang Idioma alvo (ex: "en", "es", "fr")
     * @return Texto traduzido, ou o original se a tradução falhar
     */
    public String translate(String text, String sourceLang, String targetLang) {
        if (text == null || text.isBlank()) return text;

        // Normaliza: "pt-BR" → "pt", "en-US" → "en"
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

    /**
     * Detecta o idioma de um texto usando LibreTranslate.
     *
     * @param text Texto para detectar idioma
     * @return Código do idioma detectado (ex: "pt"), ou "unknown" se falhar
     */
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
                // Retorna o idioma com maior confiança
                return json.get(0).path("language").asText("unknown");
            }
        } catch (Exception e) {
            log.error("Erro ao detectar idioma: {}", e.getMessage());
        }
        return "unknown";
    }

    // "pt-BR" → "pt", "en-US" → "en"
    private String normalizeLanguageCode(String code) {
        if (code == null) return "pt";
        return code.contains("-") ? code.split("-")[0] : code;
    }
}