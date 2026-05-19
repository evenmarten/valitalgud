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
    MISSING_FIELDS("MISSING_FIELDS", "Palun täitke kõik kohustuslikud väljad", HttpStatus.BAD_REQUEST),
    MISSING_USER_ID("MISSING_USER_ID", "Kasutaja ID on kohustuslik", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND("CART_ITEM_NOT_FOUND", "Ostukorvi rida ei leitud", HttpStatus.NOT_FOUND),
    NOT_CART_OWNER("NOT_CART_OWNER", "Teil pole õigust seda ostukorvi muuta", HttpStatus.FORBIDDEN),
    MISSING_ORDER_FIELDS("MISSING_ORDER_FIELDS", "Palun täitke kõik kohustuslikud väljad", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_FIELD_FORMAT("INVALID_ORDER_FIELD_FORMAT", "Mõni väli on vales vormingus", HttpStatus.BAD_REQUEST),
    EMPTY_CART("EMPTY_CART", "Ostukorv on tühi", HttpStatus.BAD_REQUEST),
    INVALID_QUERY_PARAMETER("INVALID_QUERY_PARAMETER", "Filtri parameeter on vales formaadis", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "See e-post on juba kasutusel", HttpStatus.CONFLICT),
    EVENT_NOT_FOUND("EVENT_NOT_FOUND", "Sündmust ei leitud", HttpStatus.NOT_FOUND),
    INVALID_REGISTRATION_STATUS("INVALID_REGISTRATION_STATUS", "Vale registreerumise staatus", HttpStatus.BAD_REQUEST),
    EVENT_FULL("EVENT_FULL", "Sündmus on osalejatega täidetud", HttpStatus.CONFLICT),
    EVENT_CANCELLED("EVENT_CANCELLED", "Sündmus on tühistatud", HttpStatus.CONFLICT),
    COMMENT_CONTENT_REQUIRED("COMMENT_CONTENT_REQUIRED", "Kommentaari sisu on kohustuslik", HttpStatus.BAD_REQUEST),
    COMMENT_TOO_LONG("COMMENT_TOO_LONG", "Kommentaar on liiga pikk (max 1000 tähemärki)", HttpStatus.BAD_REQUEST),
    NOT_ALLOWED("NOT_ALLOWED", "Sul ei ole õigust seda toimingut teha", HttpStatus.FORBIDDEN),
    INVALID_EVENT_DATA("INVALID_EVENT_DATA", "Palun täitke kõik väljad õigesti", HttpStatus.BAD_REQUEST),
    INVALID_EVENT_TIME_RANGE("INVALID_EVENT_TIME_RANGE", "Lõpuaeg peab olema hilisem kui algusaeg", HttpStatus.BAD_REQUEST),
    INVALID_EVENT_DATE("INVALID_EVENT_DATE", "Sündmuse kuupäev peab olema tulevikus", HttpStatus.BAD_REQUEST),
    INVALID_PARTICIPANTS_COUNT("INVALID_PARTICIPANTS_COUNT", "Maksimaalne osalejate arv peab olema suurem kui 0", HttpStatus.BAD_REQUEST),
    CITY_NOT_FOUND("CITY_NOT_FOUND", "Valitud linna ei leitud", HttpStatus.BAD_REQUEST),
    SKILL_TAG_NOT_FOUND("SKILL_TAG_NOT_FOUND", "Valitud oskuse-tagi ei leitud", HttpStatus.BAD_REQUEST),
    NOT_EVENT_OWNER("NOT_EVENT_OWNER", "Teil pole õigust seda sündmust muuta/tühistada", HttpStatus.FORBIDDEN),
    MAX_PARTICIPANTS_BELOW_CURRENT("MAX_PARTICIPANTS_BELOW_CURRENT", "Maksimaalne osalejate arv ei saa olla väiksem kui registreerunute arv", HttpStatus.BAD_REQUEST),
    INVALID_FILTER("INVALID_FILTER", "Vigane filtri väärtus (lubatud: THIS_WEEK, UPCOMING, ALL_FUTURE)", HttpStatus.BAD_REQUEST),
    INVALID_CALENDAR_PARAMS("INVALID_CALENDAR_PARAMS", "Vigased kalendri parameetrid", HttpStatus.BAD_REQUEST),
    INVALID_DATE_FORMAT("INVALID_DATE_FORMAT", "Vigane kuupäeva formaat (oodatud YYYY-MM-DD)", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("USER_NOT_FOUND", "Kasutajat ei leitud", HttpStatus.NOT_FOUND),
    NOT_PROFILE_OWNER("NOT_PROFILE_OWNER", "Teil pole õigust seda profiili muuta", HttpStatus.FORBIDDEN),
    INVALID_EMAIL_FORMAT("INVALID_EMAIL_FORMAT", "Vigane e-posti formaat", HttpStatus.BAD_REQUEST),
    WRONG_OLD_PASSWORD("WRONG_OLD_PASSWORD", "Praegune parool on vale", HttpStatus.BAD_REQUEST),
    PASSWORDS_DO_NOT_MATCH("PASSWORDS_DO_NOT_MATCH", "Uued paroolid ei ühti", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT("PASSWORD_TOO_SHORT", "Parool peab olema vähemalt 8 tähemärki", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorResponse(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
