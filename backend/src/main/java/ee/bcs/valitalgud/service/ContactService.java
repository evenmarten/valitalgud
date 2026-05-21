package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.contact.dto.ContactRequestDto;
import ee.bcs.valitalgud.infrastructure.config.ResendProperties;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.ServiceUnavailableException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final ResendProperties properties;
    private final RestClient resendRestClient;

    public ContactService(ResendProperties properties,
                          @Qualifier("resendRestClient") RestClient resendRestClient) {
        this.properties = properties;
        this.resendRestClient = resendRestClient;
    }

    public void sendContactMessage(ContactRequestDto request) {
        validateConfigured();
        validateRequest(request);
        Map<String, Object> emailRequest = buildEmailRequest(request);
        callResend(emailRequest);
    }

    private void validateConfigured() {
        boolean apiKeyMissing = properties.getApiKey() == null || properties.getApiKey().isBlank();
        boolean recipientMissing = properties.getToEmail() == null || properties.getToEmail().isBlank();
        if (apiKeyMissing || recipientMissing) {
            throw new ServiceUnavailableException(ErrorResponse.CONTACT_NOT_CONFIGURED);
        }
    }

    private void validateRequest(ContactRequestDto request) {
        if (request == null
                || isBlank(request.getNameOrCompany())
                || isBlank(request.getEmail())
                || isBlank(request.getMessage())) {
            throw new BadRequestException(ErrorResponse.CONTACT_FIELDS_REQUIRED);
        }
        if (!EMAIL_PATTERN.matcher(request.getEmail().strip()).matches()) {
            throw new BadRequestException(ErrorResponse.INVALID_EMAIL_FORMAT);
        }
    }

    private Map<String, Object> buildEmailRequest(ContactRequestDto request) {
        String nameOrCompany = request.getNameOrCompany().strip();
        String senderEmail = request.getEmail().strip();

        Map<String, Object> emailRequest = new LinkedHashMap<>();
        emailRequest.put("from", "Valitalgud kontaktivorm <" + properties.getFromEmail() + ">");
        emailRequest.put("to", List.of(properties.getToEmail()));
        emailRequest.put("subject", "Sissetulnud päring - " + nameOrCompany);
        emailRequest.put("reply_to", senderEmail);
        emailRequest.put("html", buildHtmlBody(nameOrCompany, senderEmail, request.getMessage().strip()));
        emailRequest.put("text", buildTextBody(nameOrCompany, senderEmail, request.getMessage().strip()));
        return emailRequest;
    }

    private void callResend(Map<String, Object> emailRequest) {
        try {
            resendRestClient.post()
                    .uri("/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(emailRequest)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.error("Resend contact email request failed", ex);
            throw new ServiceUnavailableException(ErrorResponse.CONTACT_REQUEST_FAILED);
        }
    }

    // Builds a tidy, brand-styled HTML email so the incoming request is easy to read in the inbox.
    private String buildHtmlBody(String nameOrCompany, String senderEmail, String message) {
        return """
                <div style="font-family: Arial, Helvetica, sans-serif; max-width: 560px; margin: 0 auto; \
                border: 3px solid #000; box-shadow: 6px 6px 0 #000;">
                  <div style="background:#000; color:#ffe156; padding:16px 20px; font-size:20px; font-weight:bold;">
                    Sissetulnud päring
                  </div>
                  <div style="padding: 20px; color:#000;">
                    <p style="margin:0 0 14px;"><strong>Nimi/Ettevõte:</strong><br>%s</p>
                    <p style="margin:0 0 14px;"><strong>E-mail:</strong><br><a href="mailto:%s">%s</a></p>
                    <p style="margin:0 0 6px;"><strong>Sõnum:</strong></p>
                    <div style="border:2px solid #000; padding:12px; background:#fdf6e3; white-space:pre-wrap;">%s</div>
                  </div>
                </div>
                """.formatted(
                escapeHtml(nameOrCompany),
                escapeHtml(senderEmail),
                escapeHtml(senderEmail),
                nl2br(escapeHtml(message)));
    }

    private String buildTextBody(String nameOrCompany, String senderEmail, String message) {
        return """
                Sissetulnud päring

                Nimi/Ettevõte: %s
                E-mail: %s

                Sõnum:
                %s
                """.formatted(nameOrCompany, senderEmail, message);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String escapeHtml(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String nl2br(String input) {
        return input.replace("\n", "<br>");
    }
}
