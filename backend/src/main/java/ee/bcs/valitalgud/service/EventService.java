package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.event.dto.EventResponseDto;
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

    private EventResponseDto toEventResponseDto(Event event) {
        EventResponseDto dto = eventMapper.toEventResponseDto(event);
        dto.setCurrentParticipants((int) registrationRepository.countByEventIdAndStatus(event.getId(), STATUS_GOING));
        dto.setSkillTags(event.getSkillTags().stream()
                .map(SkillTag::getName)
                .sorted()
                .toList());
        dto.setSkillTagIds(event.getSkillTags().stream()
                .map(SkillTag::getId)
                .sorted()
                .toList());
        contactRepository.findByUserId(event.getOrganizer().getId())
                .ifPresent(contact -> dto.setOrganizerName(contact.getFullName()));
        return dto;
    }
}
