package ee.bcs.valitalgud.infrastructure.exception;

import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public BadRequestException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }
}
