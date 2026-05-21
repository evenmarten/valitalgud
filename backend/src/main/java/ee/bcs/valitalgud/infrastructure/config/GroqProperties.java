package ee.bcs.valitalgud.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the Groq AI chatbot, bound from the {@code groq.*} properties.
 * The API key is read from the GROQ_API_KEY environment variable (see application.properties).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "groq")
public class GroqProperties {

    private String baseUrl = "https://api.groq.com/openai/v1";
    private String model = "openai/gpt-oss-20b";
    private String apiKey = "";
    private double temperature = 0.7;
    private int maxCompletionTokens = 1024;
}
