package ee.bcs.valitalgud.infrastructure.exception;

import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public ConflictException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }
}
