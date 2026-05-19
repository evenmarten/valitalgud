package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.calendar.dto.CalendarResponseDto;
import ee.bcs.valitalgud.controller.event.dto.EventResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.UnauthorizedException;
import ee.bcs.valitalgud.persistence.event.EventMapper;
import ee.bcs.valitalgud.persistence.event.EventRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    public CalendarResponseDto getCalendar(Integer month, Integer year, Integer userId) {
        validateUserId(userId);
        validateCalendarParams(month, year);
        List<Integer> daysWithEvents = eventRepository.findEventDaysForMonth(month, year);
        CalendarResponseDto dto = new CalendarResponseDto();
        dto.setMonth(month);
        dto.setYear(year);
        dto.setDaysWithEvents(daysWithEvents);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getDayEvents(LocalDate date, Integer userId) {
        validateUserId(userId);
        return eventRepository.findByEventDateAndNotCancelled(date)
                .stream()
                .map(eventMapper::toEventResponseDto)
                .toList();
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException(ErrorResponse.NOT_AUTHENTICATED);
        }
    }

    private void validateCalendarParams(Integer month, Integer year) {
        if (month == null || year == null || month < 1 || month > 12) {
            throw new BadRequestException(ErrorResponse.INVALID_CALENDAR_PARAMS);
        }
    }
}
