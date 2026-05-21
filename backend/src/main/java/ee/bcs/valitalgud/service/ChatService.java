package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.chat.dto.ChatMessageDto;
import ee.bcs.valitalgud.controller.chat.dto.ChatRequestDto;
import ee.bcs.valitalgud.controller.chat.dto.ChatResponseDto;
import ee.bcs.valitalgud.infrastructure.config.GroqProperties;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.ServiceUnavailableException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final GroqProperties properties;
    private final RestClient groqRestClient;
    private final String systemPrompt;

    public ChatService(GroqProperties properties,
                       @Qualifier("groqRestClient") RestClient groqRestClient,
                       @Value("classpath:chatbot-context.md") Resource contextResource) throws IOException {
        this.properties = properties;
        this.groqRestClient = groqRestClient;
        this.systemPrompt = contextResource.getContentAsString(StandardCharsets.UTF_8).strip();
    }

    public ChatResponseDto chat(ChatRequestDto request) {
        validateConfigured();
        String userMessage = validateMessage(request);
        Map<String, Object> requestBody = buildRequestBody(request, userMessage);
        GroqResponse groqResponse = callGroq(requestBody);
        return new ChatResponseDto(extractReply(groqResponse));
    }

    private void validateConfigured() {
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new ServiceUnavailableException(ErrorResponse.CHAT_NOT_CONFIGURED);
        }
    }

    private String validateMessage(ChatRequestDto request) {
        if (request == null || request.getMessage() == null || request.getMessage().isBlank()) {
            throw new BadRequestException(ErrorResponse.CHAT_MESSAGE_REQUIRED);
        }
        return request.getMessage().strip();
    }

    private Map<String, Object> buildRequestBody(ChatRequestDto request, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        addHistory(messages, request.getHistory());
        messages.add(Map.of("role", "user", "content", userMessage));

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", properties.getModel());
        requestBody.put("messages", messages);
        requestBody.put("temperature", properties.getTemperature());
        requestBody.put("max_completion_tokens", properties.getMaxCompletionTokens());
        return requestBody;
    }

    private void addHistory(List<Map<String, String>> messages, List<ChatMessageDto> history) {
        if (history == null) {
            return;
        }
        for (ChatMessageDto message : history) {
            if (isValidHistoryMessage(message)) {
                messages.add(Map.of("role", message.getRole(), "content", message.getContent()));
            }
        }
    }

    private boolean isValidHistoryMessage(ChatMessageDto message) {
        return message != null
                && ("user".equals(message.getRole()) || "assistant".equals(message.getRole()))
                && message.getContent() != null && !message.getContent().isBlank();
    }

    private GroqResponse callGroq(Map<String, Object> requestBody) {
        try {
            return groqRestClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GroqResponse.class);
        } catch (RestClientException ex) {
            log.error("Groq chat request failed", ex);
            throw new ServiceUnavailableException(ErrorResponse.CHAT_REQUEST_FAILED);
        }
    }

    private String extractReply(GroqResponse groqResponse) {
        if (groqResponse == null || groqResponse.choices() == null || groqResponse.choices().isEmpty()
                || groqResponse.choices().getFirst().message() == null) {
            log.error("Groq chat response was empty or malformed");
            throw new ServiceUnavailableException(ErrorResponse.CHAT_REQUEST_FAILED);
        }
        return groqResponse.choices().getFirst().message().content().strip();
    }

    // Minimal mapping of the Groq (OpenAI-compatible) chat completion response — only the fields we use.
    private record GroqResponse(List<GroqChoice> choices) {
    }

    private record GroqChoice(GroqMessage message) {
    }

    private record GroqMessage(String role, String content) {
    }
}
