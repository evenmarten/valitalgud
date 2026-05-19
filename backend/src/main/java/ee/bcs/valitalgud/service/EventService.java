package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.event.dto.EventDetailsResponseDto;
import ee.bcs.valitalgud.controller.event.dto.EventResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.NotFoundException;
import ee.bcs.valitalgud.infrastructure.exception.UnauthorizedException;
import ee.bcs.valitalgud.persistence.contact.Contact;
import ee.bcs.valitalgud.persistence.contact.ContactRepository;
import ee.bcs.valitalgud.persistence.event.Event;
import ee.bcs.valitalgud.persistence.event.EventMapper;
import ee.bcs.valitalgud.persistence.event.EventRepository;
import ee.bcs.valitalgud.persistence.registration.RegistrationRepository;
import ee.bcs.valitalgud.persistence.skilltag.SkillTag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final String STATUS_GOING = "LAHEB";

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final ContactRepository contactRepository;
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    public List<EventResponseDto> getFilteredEvents(Integer cityId, Integer skillTagId, LocalDate fromDate) {
        return eventRepository.findFilteredEvents(cityId, skillTagId, fromDate)
                .stream()
                .map(this::toEventResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventDetailsResponseDto getEventDetails(Integer eventId, Integer userId) {
        validateUserId(userId);
        Event event = getValidEventBy(eventId);
        return buildEventDetailsResponseDto(event, userId);
    }

    Event getValidEventBy(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorResponse.EVENT_NOT_FOUND));
    }

    private EventDetailsResponseDto buildEventDetailsResponseDto(Event event, Integer userId) {
        EventDetailsResponseDto dto = eventMapper.toEventDetailsResponseDto(event);
        dto.setCurrentParticipants((int) registrationRepository.countByEventIdAndStatus(event.getId(), STATUS_GOING));
        dto.setSkillTags(event.getSkillTags().stream().map(SkillTag::getName).sorted().toList());
        contactRepository.findByUserId(event.getOrganizer().getId()).ifPresent(contact -> {
            dto.setOrganizerName(contact.getFullName());
            dto.setOrganizerEmail(contact.getEmail());
        });
        registrationRepository.findByUserIdAndEventId(userId, event.getId())
                .ifPresent(reg -> dto.setUserRegistrationStatus(reg.getStatus()));
        return dto;
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException(ErrorResponse.NOT_AUTHENTICATED);
        }
    }

    private EventResponseDto toEventResponseDto(Event event) {
        EventResponseDto dto = eventMapper.toEventResponseDto(event);
        dto.setCurrentParticipants((int) registrationRepository.countByEventIdAndStatus(event.getId(), STATUS_GOING));
        dto.setSkillTags(event.getSkillTags().stream().map(SkillTag::getName).sorted().toList());
        dto.setSkillTagIds(event.getSkillTags().stream().map(SkillTag::getId).sorted().toList());
        contactRepository.findByUserId(event.getOrganizer().getId())
                .ifPresent(contact -> dto.setOrganizerName(contact.getFullName()));
        return dto;
    }
}
