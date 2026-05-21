package ee.bcs.valitalgud.infrastructure.error;

import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.ConflictException;
import ee.bcs.valitalgud.infrastructure.exception.ForbiddenException;
import ee.bcs.valitalgud.infrastructure.exception.NotFoundException;
import ee.bcs.valitalgud.infrastructure.exception.ServiceUnavailableException;
import ee.bcs.valitalgud.infrastructure.exception.UnauthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return buildResponse(ex.getErrorResponse());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        return buildResponse(ex.getErrorResponse());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleServiceUnavailable(ServiceUnavailableException ex) {
        return buildResponse(ex.getErrorResponse());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        if (requiredType != null && java.time.LocalDate.class.isAssignableFrom(requiredType)) {
            return buildResponse(ErrorResponse.INVALID_DATE_FORMAT);
        }
        return buildResponse(ErrorResponse.INVALID_QUERY_PARAMETER);
    }

    private ResponseEntity<ApiError> buildResponse(ErrorResponse errorResponse) {
        ApiError body = ApiError.builder()
                .code(errorResponse.getCode())
                .message(errorResponse.getMessage())
                .build();
        return ResponseEntity.status(errorResponse.getHttpStatus()).body(body);
    }
}
