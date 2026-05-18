package ee.bcs.valitalgud.infrastructure.exception;

import lombok.Getter;

@Getter
public class PrimaryKeyNotFoundException extends RuntimeException {
    private final String message;
    private final Integer errorCode;

    public PrimaryKeyNotFoundException(String fieldName, Object fieldValue) {
        super(fieldName + " = " + fieldValue + " not found");
        this.message = fieldName + " = " + fieldValue + " not found";
        this.errorCode = 0;
    }
}