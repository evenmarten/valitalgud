package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.login.dto.LoginDto;
import ee.bcs.valitalgud.controller.login.dto.LoginResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.ForbiddenException;
import ee.bcs.valitalgud.infrastructure.exception.UnauthorizedException;
import ee.bcs.valitalgud.persistence.contact.Contact;
import ee.bcs.valitalgud.persistence.contact.ContactRepository;
import ee.bcs.valitalgud.persistence.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final ContactRepository contactRepository;

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginDto loginDto) {
        validateCredentialsPresent(loginDto);

        Contact contact = contactRepository.findByEmailIgnoreCase(loginDto.getEmail().trim())
                .orElseThrow(() -> new UnauthorizedException(ErrorResponse.INVALID_CREDENTIALS));

        User user = contact.getUser();

        if (!user.getPassword().equals(loginDto.getPassword())) {
            throw new UnauthorizedException(ErrorResponse.INVALID_CREDENTIALS);
        }

        if (!ACTIVE_STATUS.equalsIgnoreCase(user.getStatus())) {
            throw new ForbiddenException(ErrorResponse.ACCOUNT_BLOCKED);
        }

        return toResponse(user, contact);
    }

    private void validateCredentialsPresent(LoginDto loginDto) {
        if (loginDto == null
                || isBlank(loginDto.getEmail())
                || isBlank(loginDto.getPassword())) {
            throw new BadRequestException(ErrorResponse.MISSING_CREDENTIALS);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private LoginResponseDto toResponse(User user, Contact contact) {
        String[] nameParts = splitFullName(contact.getFullName());
        return LoginResponseDto.builder()
                .userId(user.getId().longValue())
                .firstName(nameParts[0])
                .middleName(nameParts[1])
                .lastName(nameParts[2])
                .role(user.getRole().getName())
                .build();
    }

    // Returns [firstName, middleName, lastName]; middleName may be null.
    private String[] splitFullName(String fullName) {
        String trimmed = fullName == null ? "" : fullName.trim();
        if (trimmed.isEmpty()) {
            return new String[]{"", null, ""};
        }
        String[] parts = trimmed.split("\\s+");
        if (parts.length == 1) {
            return new String[]{parts[0], null, ""};
        }
        if (parts.length == 2) {
            return new String[]{parts[0], null, parts[1]};
        }
        String first = parts[0];
        String last = parts[parts.length - 1];
        StringBuilder middle = new StringBuilder(parts[1]);
        for (int i = 2; i < parts.length - 1; i++) {
            middle.append(' ').append(parts[i]);
        }
        return new String[]{first, middle.toString(), last};
    }
}
