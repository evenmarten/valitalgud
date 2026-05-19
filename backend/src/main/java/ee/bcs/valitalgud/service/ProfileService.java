package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.profile.dto.ChangePasswordDto;
import ee.bcs.valitalgud.controller.profile.dto.ProfileResponseDto;
import ee.bcs.valitalgud.controller.profile.dto.UpdateProfileDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.ConflictException;
import ee.bcs.valitalgud.infrastructure.exception.ForbiddenException;
import ee.bcs.valitalgud.infrastructure.exception.NotFoundException;
import ee.bcs.valitalgud.infrastructure.exception.UnauthorizedException;
import ee.bcs.valitalgud.persistence.contact.Contact;
import ee.bcs.valitalgud.persistence.contact.ContactRepository;
import ee.bcs.valitalgud.persistence.user.User;
import ee.bcs.valitalgud.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String DELETED_STATUS = "DELETED";
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Integer userId, Integer requesterId) {
        validateOwnerOrAdmin(userId, requesterId);
        User user = getValidUserBy(userId);
        Contact contact = getValidContactBy(userId);
        return buildProfileResponseDto(user, contact);
    }

    @Transactional
    public ProfileResponseDto updateProfile(Integer userId, Integer requesterId, UpdateProfileDto dto) {
        validateOwner(userId, requesterId);
        validateUpdateProfileFields(dto);
        validateEmailFormat(dto.getEmail().trim());
        validateEmailAvailable(dto.getEmail().trim(), userId);

        User user = getValidUserBy(userId);
        Contact contact = getValidContactBy(userId);
        contact.setFullName(buildFullName(dto));
        contact.setEmail(dto.getEmail().trim());
        contact.setPhone(dto.getPhone());
        contactRepository.save(contact);

        return buildProfileResponseDto(user, contact);
    }

    @Transactional
    public void changePassword(Integer userId, Integer requesterId, ChangePasswordDto dto) {
        validateOwner(userId, requesterId);
        validateChangePasswordFields(dto);

        User user = getValidUserBy(userId);
        validateOldPassword(user, dto.getOldPassword());
        validateNewPasswords(dto.getNewPassword(), dto.getConfirmNewPassword());

        user.setPassword(dto.getNewPassword());
        userRepository.save(user);
    }

    @Transactional
    public void deleteProfile(Integer userId, Integer requesterId) {
        validateOwnerOrAdmin(userId, requesterId);
        User user = getValidUserBy(userId);
        user.setStatus(DELETED_STATUS);
        userRepository.save(user);
    }

    private User getValidUserBy(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorResponse.USER_NOT_FOUND));
    }

    private Contact getValidContactBy(Integer userId) {
        return contactRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorResponse.USER_NOT_FOUND));
    }

    private void validateOwner(Integer userId, Integer requesterId) {
        if (requesterId == null) {
            throw new UnauthorizedException(ErrorResponse.NOT_AUTHENTICATED);
        }
        if (!requesterId.equals(userId)) {
            throw new ForbiddenException(ErrorResponse.NOT_PROFILE_OWNER);
        }
    }

    private void validateOwnerOrAdmin(Integer userId, Integer requesterId) {
        if (requesterId == null) {
            throw new UnauthorizedException(ErrorResponse.NOT_AUTHENTICATED);
        }
        if (requesterId.equals(userId)) {
            return;
        }
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UnauthorizedException(ErrorResponse.NOT_AUTHENTICATED));
        if (!ADMIN_ROLE.equalsIgnoreCase(requester.getRole().getName())) {
            throw new ForbiddenException(ErrorResponse.NOT_PROFILE_OWNER);
        }
    }

    private void validateUpdateProfileFields(UpdateProfileDto dto) {
        if (dto == null
                || isBlank(dto.getFirstName())
                || isBlank(dto.getLastName())
                || isBlank(dto.getEmail())) {
            throw new BadRequestException(ErrorResponse.MISSING_FIELDS);
        }
    }

    private void validateChangePasswordFields(ChangePasswordDto dto) {
        if (dto == null
                || isBlank(dto.getOldPassword())
                || isBlank(dto.getNewPassword())
                || isBlank(dto.getConfirmNewPassword())) {
            throw new BadRequestException(ErrorResponse.MISSING_FIELDS);
        }
    }

    private void validateEmailFormat(String email) {
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new BadRequestException(ErrorResponse.INVALID_EMAIL_FORMAT);
        }
    }

    private void validateEmailAvailable(String email, Integer currentUserId) {
        contactRepository.findByEmailIgnoreCase(email).ifPresent(existing -> {
            if (!existing.getUser().getId().equals(currentUserId)) {
                throw new ConflictException(ErrorResponse.EMAIL_ALREADY_EXISTS);
            }
        });
    }

    private void validateOldPassword(User user, String oldPassword) {
        if (!user.getPassword().equals(oldPassword)) {
            throw new BadRequestException(ErrorResponse.WRONG_OLD_PASSWORD);
        }
    }

    private void validateNewPasswords(String newPassword, String confirmNewPassword) {
        if (!newPassword.equals(confirmNewPassword)) {
            throw new BadRequestException(ErrorResponse.PASSWORDS_DO_NOT_MATCH);
        }
        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new BadRequestException(ErrorResponse.PASSWORD_TOO_SHORT);
        }
    }

    private ProfileResponseDto buildProfileResponseDto(User user, Contact contact) {
        ProfileResponseDto dto = new ProfileResponseDto();
        dto.setUserId(user.getId());
        dto.setFullName(contact.getFullName());
        dto.setEmail(contact.getEmail());
        dto.setPhone(contact.getPhone());
        dto.setRole(user.getRole().getName());
        dto.setDescription(null);
        return dto;
    }

    private String buildFullName(UpdateProfileDto dto) {
        StringBuilder sb = new StringBuilder(dto.getFirstName().trim());
        if (!isBlank(dto.getMiddleName())) {
            sb.append(' ').append(dto.getMiddleName().trim());
        }
        sb.append(' ').append(dto.getLastName().trim());
        return sb.toString();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
