package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.registration.dto.RegistrationDto;
import ee.bcs.valitalgud.controller.registration.dto.RegistrationResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.ConflictException;
import ee.bcs.valitalgud.persistence.event.Event;
import ee.bcs.valitalgud.persistence.registration.Registration;
import ee.bcs.valitalgud.persistence.registration.RegistrationRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private static final String STATUS_GOING = "LAHEB";
    private static final List<String> VALID_STATUSES = List.of("LAHEB", "VOIB_OLLA", "EI_LAHE");

    private final RegistrationRepository registrationRepository;
    private final EventService eventService;
    private final UserValidationService userValidationService;

    @Transactional
    public RegistrationResponseDto register(Integer eventId, Integer userId, RegistrationDto registrationDto) {
        validateUserId(userId);
        validateStatus(registrationDto.getStatus());

        Event event = eventService.getValidEventBy(eventId);
        validateEventNotCancelled(event);

        Optional<Registration> existing = registrationRepository.findByUserIdAndEventId(userId, eventId);

        if (existing.isEmpty() && STATUS_GOING.equals(registrationDto.getStatus())) {
            validateEventNotFull(event);
        }

        Registration registration = existing.map(reg -> {
            reg.setStatus(registrationDto.getStatus());
            return registrationRepository.save(reg);
        }).orElseGet(() -> {
            Registration newReg = new Registration();
            newReg.setUserId(userId);
            newReg.setEventId(eventId);
            newReg.setStatus(registrationDto.getStatus());
            return registrationRepository.save(newReg);
        });

        Registration saved = registrationRepository.findById(registration.getId()).orElseThrow();
        return toRegistrationResponseDto(saved);
    }

    @Transactional
    public void cancelRegistration(Integer eventId, Integer userId) {
        validateUserId(userId);
        registrationRepository.deleteByUserIdAndEventId(userId, eventId);
    }

    public boolean isNewRegistration(Integer eventId, Integer userId) {
        return registrationRepository.findByUserIdAndEventId(userId, eventId).isEmpty();
    }

    private void validateUserId(Integer userId) {
        userValidationService.validateActiveUser(userId);
    }

    private void validateStatus(String status) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new BadRequestException(ErrorResponse.INVALID_REGISTRATION_STATUS);
        }
    }

    private void validateEventNotCancelled(Event event) {
        if (Boolean.TRUE.equals(event.getIsCancelled())) {
            throw new ConflictException(ErrorResponse.EVENT_CANCELLED);
        }
    }

    private void validateEventNotFull(Event event) {
        if (event.getMaxParticipants() == null) {
            return;
        }
        long currentCount = registrationRepository.countByEventIdAndStatus(event.getId(), STATUS_GOING);
        if (currentCount >= event.getMaxParticipants()) {
            throw new ConflictException(ErrorResponse.EVENT_FULL);
        }
    }

    private RegistrationResponseDto toRegistrationResponseDto(Registration registration) {
        RegistrationResponseDto dto = new RegistrationResponseDto();
        dto.setRegistrationId(registration.getId());
        dto.setEventId(registration.getEventId());
        dto.setUserId(registration.getUserId());
        dto.setStatus(registration.getStatus());
        if (registration.getRegisteredAt() != null) {
            dto.setRegisteredAt(LocalDateTime.ofInstant(registration.getRegisteredAt(), ZoneOffset.UTC));
        }
        return dto;
    }
}
