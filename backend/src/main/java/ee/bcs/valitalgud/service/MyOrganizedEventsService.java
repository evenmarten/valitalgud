package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.event.dto.OrganizedEventResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.persistence.event.EventRepository;
import ee.bcs.valitalgud.persistence.event.OrganizedEventProjection;
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

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<OrganizedEventResponseDto> findMyOrganizedEvents(Integer userId, String city, String skillTag, LocalDate date) {
        validateUserId(userId);
        List<OrganizedEventProjection> projections = eventRepository
                .findOrganizedEventsBy(userId, blankToNull(city), blankToNull(skillTag), date);
        return projections.stream().map(this::toOrganizedEventResponseDto).toList();
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new BadRequestException(ErrorResponse.MISSING_USER_ID);
        }
    }

    private String blankToNull(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }

    private OrganizedEventResponseDto toOrganizedEventResponseDto(OrganizedEventProjection projection) {
        return new OrganizedEventResponseDto(
                projection.getEventId(),
                projection.getTitle(),
                projection.getDate(),
                projection.getCity(),
                buildStatus(projection),
                projection.getCurrentParticipants() == null ? 0 : projection.getCurrentParticipants().intValue(),
                projection.getMaxParticipants()
        );
    }

    private String buildStatus(OrganizedEventProjection projection) {
        if (Boolean.TRUE.equals(projection.getIsCancelled())) {
            return STATUS_CANCELLED;
        }
        if (projection.getDate().isBefore(LocalDate.now())) {
            return STATUS_PAST;
        }
        return STATUS_ACTIVE;
    }
}
