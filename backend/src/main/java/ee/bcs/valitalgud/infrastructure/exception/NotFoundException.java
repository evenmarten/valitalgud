package ee.bcs.valitalgud.infrastructure.exception;

import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public NotFoundException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }
}