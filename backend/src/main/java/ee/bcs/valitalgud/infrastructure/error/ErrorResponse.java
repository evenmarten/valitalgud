package ee.bcs.valitalgud.infrastructure.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorResponse {

    MISSING_CREDENTIALS("MISSING_CREDENTIALS", "Palun täitke kõik väljad", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Vale email või parool", HttpStatus.UNAUTHORIZED),
    ACCOUNT_BLOCKED("ACCOUNT_BLOCKED", "Teie konto on blokeeritud. Pöörduge administraatori poole.", HttpStatus.FORBIDDEN),
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "Toodet ei leitud", HttpStatus.NOT_FOUND),
    INVALID_QUANTITY("INVALID_QUANTITY", "Kogus peab olema suurem kui null", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK", "Laos pole piisavalt tooteid", HttpStatus.BAD_REQUEST),
    NOT_AUTHENTICATED("NOT_AUTHENTICATED", "Palun logige sisse", HttpStatus.UNAUTHORIZED),
    MISSING_FIELDS("MISSING_FIELDS", "Palun täitke kõik kohustuslikud väljad", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorResponse(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
