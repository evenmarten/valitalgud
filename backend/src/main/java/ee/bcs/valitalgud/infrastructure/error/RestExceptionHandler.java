package ee.bcs.valitalgud.infrastructure.error;

import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.ForbiddenException;
import ee.bcs.valitalgud.infrastructure.exception.UnauthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        return buildResponse(ex.getErrorResponse());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex) {
        return buildResponse(ex.getErrorResponse());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenException ex) {
        return buildResponse(ex.getErrorResponse());
    }

    private ResponseEntity<ApiError> buildResponse(ErrorResponse errorResponse) {
        ApiError body = ApiError.builder()
                .code(errorResponse.getCode())
                .message(errorResponse.getMessage())
                .build();
        return ResponseEntity.status(errorResponse.getHttpStatus()).body(body);
    }
}
