package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.event.dto.OrganizedEventResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.persistence.event.Event;
import ee.bcs.valitalgud.persistence.registration.RegistrationRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyOrganizedEventsService {

    private static final String STATUS_ACTIVE = "Aktiivne";
    private static final String STATUS_PAST = "Lõppenud";
    private static final String STATUS_CANCELLED = "Tühistatud";
    private static final String REGISTRATION_GOING = "LAHEB";

    private final EventService eventService;
    private final RegistrationRepository registrationRepository;
    private final UserValidationService userValidationService;

    @Transactional(readOnly = true)
    public List<OrganizedEventResponseDto> findMyOrganizedEvents(Integer userId, Integer cityId, Integer skillTagId, LocalDate date) {
        validateUserId(userId);
        List<Event> events = eventService.findOrganizedEvents(userId, cityId, skillTagId, date);
        return events.stream().map(this::toOrganizedEventResponseDto).toList();
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new BadRequestException(ErrorResponse.MISSING_USER_ID);
        }
        userValidationService.ensureExistsAndActive(userId);
    }

    private OrganizedEventResponseDto toOrganizedEventResponseDto(Event event) {
        return new OrganizedEventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getEventDate(),
                event.getCity().getName(),
                buildStatus(event),
                (int) registrationRepository.countByEventIdAndStatus(event.getId(), REGISTRATION_GOING),
                event.getMaxParticipants()
        );
    }

    private String buildStatus(Event event) {
        if (Boolean.TRUE.equals(event.getIsCancelled())) {
            return STATUS_CANCELLED;
        }
        if (event.getEventDate().isBefore(LocalDate.now())) {
            return STATUS_PAST;
        }
        return STATUS_ACTIVE;
    }
}
