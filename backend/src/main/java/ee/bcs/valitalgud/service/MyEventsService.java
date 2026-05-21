package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.myevents.dto.MyEventResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.persistence.registration.MyEventProjection;
import ee.bcs.valitalgud.persistence.registration.RegistrationRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyEventsService {

    private static final String FILTER_UPCOMING = "UPCOMING";
    private static final String FILTER_ALL_FUTURE = "ALL_FUTURE";

    private final RegistrationRepository registrationRepository;
    private final UserValidationService userValidationService;

    @Transactional(readOnly = true)
    public List<MyEventResponseDto> findMyEvents(Integer userId, String filter, Integer cityId,
            Integer countyId, Integer skillTagId, LocalDate fromDate) {
        validateUserId(userId);
        validateFilter(filter);

        DateRange range = buildDateRange(filter);
        List<MyEventProjection> projections = registrationRepository
                .findMyEventsBy(userId, range.fromDate(), range.toDate(), cityId, countyId, skillTagId, fromDate);
        return projections.stream().map(this::toMyEventResponseDto).toList();
    }

    private DateRange buildDateRange(String filter) {
        LocalDate today = LocalDate.now();
        return switch (filter) {
            case FILTER_UPCOMING -> new DateRange(today.plusDays(1), null);
            case FILTER_ALL_FUTURE -> new DateRange(today, null);
            default -> throw new BadRequestException(ErrorResponse.INVALID_FILTER);
        };
    }

    private void validateUserId(Integer userId) {
        userValidationService.validateActiveUser(userId);
    }

    private void validateFilter(String filter) {
        if (filter == null
                || (!FILTER_UPCOMING.equals(filter)
                && !FILTER_ALL_FUTURE.equals(filter))) {
            throw new BadRequestException(ErrorResponse.INVALID_FILTER);
        }
    }

    private MyEventResponseDto toMyEventResponseDto(MyEventProjection projection) {
        return new MyEventResponseDto(
                projection.getEventId(),
                projection.getTitle(),
                projection.getDate(),
                projection.getLocation(),
                projection.getCounty(),
                projection.getDescription(),
                projection.getUserRegistrationStatus()
        );
    }

    private record DateRange(LocalDate fromDate, LocalDate toDate) {
    }
}
