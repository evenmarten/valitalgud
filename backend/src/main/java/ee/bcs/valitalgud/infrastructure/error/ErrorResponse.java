package ee.bcs.valitalgud.infrastructure.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorResponse {

    MISSING_CREDENTIALS("MISSING_CREDENTIALS", "Palun täitke kõik väljad", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Vale email või parool", HttpStatus.UNAUTHORIZED),
    ACCOUNT_BLOCKED("ACCOUNT_BLOCKED", "Teie konto on blokeeritud. Pöörduge administraatori poole.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorResponse(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
