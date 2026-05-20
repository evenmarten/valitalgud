package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.login.dto.LoginResponseDto;
import ee.bcs.valitalgud.controller.register.dto.RegisterDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.ConflictException;
import ee.bcs.valitalgud.persistence.contact.Contact;
import ee.bcs.valitalgud.persistence.contact.ContactRepository;
import ee.bcs.valitalgud.persistence.role.Role;
import ee.bcs.valitalgud.persistence.role.RoleRepository;
import ee.bcs.valitalgud.persistence.user.User;
import ee.bcs.valitalgud.persistence.user.UserMapper;
import ee.bcs.valitalgud.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private static final String USER_ROLE_NAME = "USER";
    private static final String ACTIVE_STATUS = "ACTIVE";

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Transactional
    public LoginResponseDto register(RegisterDto registerDto) {
        validateRequiredFields(registerDto);
        validateEmailIsAvailable(registerDto.getEmail());

        User user = createAndSaveUser(registerDto.getPassword());
        Contact contact = createAndSaveContact(registerDto, user);

        return userMapper.toLoginResponseDto(user, contact);
    }

    private void validateRequiredFields(RegisterDto registerDto) {
        if (registerDto == null
                || isBlank(registerDto.getFullName())
                || isBlank(registerDto.getEmail())
                || isBlank(registerDto.getPassword())) {
            throw new BadRequestException(ErrorResponse.MISSING_FIELDS);
        }
    }

    private void validateEmailIsAvailable(String email) {
        contactRepository.findByEmailIgnoreCase(email.trim())
                .ifPresent(existing -> {
                    throw new ConflictException(ErrorResponse.EMAIL_ALREADY_EXISTS);
                });
    }

    private User createAndSaveUser(String password) {
        Role userRole = roleRepository.findByName(USER_ROLE_NAME)
                .orElseThrow(() -> new IllegalStateException("Roll USER ei leitud andmebaasist"));

        User user = new User();
        user.setPassword(password);
        user.setStatus(ACTIVE_STATUS);
        user.setRole(userRole);

        return userRepository.save(user);
    }

    private Contact createAndSaveContact(RegisterDto registerDto, User user) {
        Contact contact = new Contact();
        contact.setUser(user);
        contact.setFullName(registerDto.getFullName().trim());
        contact.setEmail(registerDto.getEmail().trim());
        contact.setPhone(registerDto.getPhone());
        contact.setDescription(registerDto.getDescription());

        return contactRepository.save(contact);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
