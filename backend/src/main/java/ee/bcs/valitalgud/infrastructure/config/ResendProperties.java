package ee.bcs.valitalgud.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for sending contact-form emails via the Resend API, bound from the {@code resend.*} properties.
 * The API key is read from the RESEND_API_KEY environment variable (see application.properties and backend/.env).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "resend")
public class ResendProperties {

    private String baseUrl = "https://api.resend.com";
    private String apiKey = "";
    // Sender address. Use "onboarding@resend.dev" for testing, or a verified domain in production.
    private String fromEmail = "onboarding@resend.dev";
    // Recipient address — where contact-form submissions are delivered.
    private String toEmail = "";
}