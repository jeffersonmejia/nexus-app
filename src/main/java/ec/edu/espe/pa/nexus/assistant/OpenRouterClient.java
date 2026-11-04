package ec.edu.espe.pa.nexus.assistant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenRouterClient {
    private static final Logger log = LoggerFactory.getLogger(OpenRouterClient.class);

    private final ObjectMapper objectMapper;
    private final RestClient client;
    private final String apiKey;
    private final String model;
    private final String siteUrl;
    private final String appName;

    public OpenRouterClient(ObjectMapper objectMapper,
                            @Value("${openrouter.base-url:https://openrouter.ai/api/v1}") String baseUrl,
                            @Value("${openrouter.api-key:}") String apiKey,
                            @Value("${openrouter.model:openrouter/auto}") String model,
                            @Value("${openrouter.site-url:http://localhost:5173}") String siteUrl,
                            @Value("${openrouter.app-name:Nexus Study}") String appName) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.siteUrl = siteUrl;
        this.appName = appName;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(15));
        factory.setReadTimeout(Duration.ofSeconds(90));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String answer(String question, String context) {
        if (!isConfigured()) {
            throw new IllegalStateException(
                    "Configura OPENROUTER_API_KEY en el archivo .env para habilitar el asistente IA");
        }

        String systemPrompt = """
                Eres Nexus Study, un asistente académico para estudiantes universitarios.
                Responde siempre en español, de forma clara, ordenada y práctica.
                Puedes crear planes de estudio, explicar conceptos, resumir documentos y proponer horarios.
                Si el estudiante adjunta el texto de un documento, básate en ese contenido y cita ideas clave.
                Si no sabes algo, dilo con honestidad y sugiere cómo investigarlo.
                Usa listas y secciones cortas cuando ayuden a estudiar mejor.
                """;

        StringBuilder userPrompt = new StringBuilder();
        userPrompt.append("Pregunta del estudiante:\n").append(question);
        if (context != null && !context.isBlank()) {
            String excerpt = context.substring(0, Math.min(context.length(), 8_000));
            userPrompt.append("\n\nTexto del documento para consultar:\n").append(excerpt);
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", model);
        payload.put("temperature", 0.6);
        payload.put("max_tokens", 1_200);
        payload.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt.toString())));

        try {
            String raw = client.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("HTTP-Referer", siteUrl)
                    .header("X-Title", appName)
                    .body(objectMapper.writeValueAsString(payload))
                    .retrieve()
                    .body(String.class);
            JsonNode root = objectMapper.readTree(raw == null ? "{}" : raw);
            String content = root.path("choices").path(0).path("message").path("content").asText("");
            if (content.isBlank()) {
                String providerError = root.path("error").path("message").asText("respuesta vacía del proveedor");
                throw new IllegalStateException(providerError);
            }
            return content.trim();
        } catch (Exception exception) {
            log.warn("Error al consultar OpenRouter: {}", exception.getMessage());
            throw new IllegalStateException("No se pudo obtener respuesta del proveedor de IA", exception);
        }
    }
}
