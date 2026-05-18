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
import ee.bcs.valitalgud.persistence.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final ContactRepository contactRepository;
    private final UserMapper userMapper;

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

        return userMapper.toLoginResponseDto(user, contact);
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
}
