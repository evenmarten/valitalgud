package ee.bcs.valitalgud.infrastructure.error;

import lombok.Getter;

@Getter
public enum ErrorResponse {
    ;

    private final String message;
    private final Integer errorCode;

    ErrorResponse(String message, Integer errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }
}