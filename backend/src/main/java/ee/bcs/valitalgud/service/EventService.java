package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.event.dto.CreateEventDto;
import ee.bcs.valitalgud.controller.event.dto.EventDetailsResponseDto;
import ee.bcs.valitalgud.controller.event.dto.EventResponseDto;
import ee.bcs.valitalgud.controller.event.dto.UpdateEventDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.ForbiddenException;
import ee.bcs.valitalgud.infrastructure.exception.NotFoundException;
import ee.bcs.valitalgud.infrastructure.exception.UnauthorizedException;
import ee.bcs.valitalgud.persistence.city.City;
import ee.bcs.valitalgud.persistence.contact.ContactRepository;
import ee.bcs.valitalgud.persistence.event.Event;
import ee.bcs.valitalgud.persistence.event.EventMapper;
import ee.bcs.valitalgud.persistence.event.EventRepository;
import ee.bcs.valitalgud.persistence.registration.RegistrationRepository;
import ee.bcs.valitalgud.persistence.skilltag.SkillTag;
import ee.bcs.valitalgud.persistence.user.User;
import ee.bcs.valitalgud.persistence.user.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final String STATUS_GOING = "LAHEB";
    private static final String ADMIN_ROLE = "ADMIN";

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final CityService cityService;
    private final SkillTagService skillTagService;
    private final UserValidationService userValidationService;
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    public List<EventResponseDto> getFilteredEvents(Integer cityId, Integer countyId, Integer skillTagId, LocalDate fromDate) {
        return eventRepository.findFilteredEvents(cityId, countyId, skillTagId, fromDate, null, null, false)
                .stream()
                .map(this::toEventResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Event> findOrganizedEvents(Integer organizerId, Integer cityId, Integer countyId, Integer skillTagId, LocalDate date) {
        return eventRepository.findFilteredEvents(cityId, countyId, skillTagId, date, date, organizerId, true);
    }

    @Transactional(readOnly = true)
    public EventDetailsResponseDto getEventDetails(Integer eventId, Integer userId) {
        // Sündmuse detailvaade on avalik — sisselogimata kasutaja näeb sündmust, aga ilma registreerumiseta.
        if (userId != null) {
            validateUserId(userId);
        }
        Event event = getValidEventBy(eventId);
        return buildEventDetailsResponseDto(event, userId);
    }

    @Transactional(readOnly = true)
    public EventResponseDto getEventForEdit(Integer eventId, Integer userId) {
        validateUserId(userId);
        Event event = getValidEventBy(eventId);
        return toEventResponseDto(event);
    }

    @Transactional
    public EventResponseDto createEvent(CreateEventDto createEventDto, Integer userId) {
        validateUserId(userId);
        validateCreateEventFields(createEventDto);
        validateEventDateInFuture(createEventDto.getDate());
        validateTimeRange(createEventDto.getStartTime(), createEventDto.getEndTime());
        validateParticipantsCount(createEventDto.getMaxParticipants());

        City city = cityService.getValidCityBy(createEventDto.getCityId());
        User organizer = getValidUserBy(userId);
        Set<SkillTag> skillTags = skillTagService.getValidSkillTagsBy(createEventDto.getSkillTagIds());

        Event event = createAndSaveEvent(createEventDto, organizer, city, skillTags);
        return toEventResponseDto(event);
    }

    @Transactional
    public EventResponseDto updateEvent(Integer eventId, UpdateEventDto updateEventDto, Integer userId) {
        validateUserId(userId);
        Event event = getValidEventBy(eventId);
        validateOwnershipOrAdmin(event, userId);
        validateUpdateEventFields(updateEventDto);
        validateEventDateInFuture(updateEventDto.getDate());
        validateTimeRange(updateEventDto.getStartTime(), updateEventDto.getEndTime());
        validateMaxParticipantsAgainstCurrent(event.getId(), updateEventDto.getMaxParticipants());

        Set<SkillTag> skillTags = skillTagService.getValidSkillTagsBy(updateEventDto.getSkillTagIds());
        applyUpdate(event, updateEventDto, skillTags);
        Event saved = eventRepository.save(event);
        return toEventResponseDto(saved);
    }

    @Transactional
    public void deleteEvent(Integer eventId, Integer userId) {
        validateUserId(userId);
        Event event = getValidEventBy(eventId);
        validateOwnershipOrAdmin(event, userId);
        event.setIsCancelled(true);
        eventRepository.save(event);
    }

    Event getValidEventBy(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorResponse.EVENT_NOT_FOUND));
    }

    private User getValidUserBy(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorResponse.NOT_AUTHENTICATED));
    }

    private Event createAndSaveEvent(CreateEventDto dto, User organizer, City city, Set<SkillTag> skillTags) {
        Event event = eventMapper.toEvent(dto);
        event.setOrganizer(organizer);
        event.setCity(city);
        event.setSkillTags(skillTags);
        event.setIsCancelled(false);
        return eventRepository.save(event);
    }

    private void applyUpdate(Event event, UpdateEventDto dto, Set<SkillTag> skillTags) {
        eventMapper.updateEventFromDto(dto, event);
        event.setSkillTags(skillTags);
    }

    private void validateCreateEventFields(CreateEventDto dto) {
        if (dto == null
                || isBlank(dto.getTitle())
                || dto.getCityId() == null
                || isBlank(dto.getAddress())
                || dto.getDate() == null
                || dto.getStartTime() == null
                || dto.getEndTime() == null
                || dto.getMaxParticipants() == null) {
            throw new BadRequestException(ErrorResponse.INVALID_EVENT_DATA);
        }
    }

    private void validateUpdateEventFields(UpdateEventDto dto) {
        if (dto == null
                || isBlank(dto.getTitle())
                || isBlank(dto.getAddress())
                || dto.getDate() == null
                || dto.getStartTime() == null
                || dto.getEndTime() == null
                || dto.getMaxParticipants() == null) {
            throw new BadRequestException(ErrorResponse.INVALID_EVENT_DATA);
        }
    }

    private void validateEventDateInFuture(LocalDate date) {
        if (!date.isAfter(LocalDate.now())) {
            throw new BadRequestException(ErrorResponse.INVALID_EVENT_DATE);
        }
    }

    private void validateTimeRange(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException(ErrorResponse.INVALID_EVENT_TIME_RANGE);
        }
    }

    private void validateParticipantsCount(Integer maxParticipants) {
        if (maxParticipants <= 0) {
            throw new BadRequestException(ErrorResponse.INVALID_PARTICIPANTS_COUNT);
        }
    }

    private void validateMaxParticipantsAgainstCurrent(Integer eventId, Integer maxParticipants) {
        if (maxParticipants <= 0) {
            throw new BadRequestException(ErrorResponse.INVALID_PARTICIPANTS_COUNT);
        }
        long currentParticipants = registrationRepository.countByEventIdAndStatus(eventId, STATUS_GOING);
        if (maxParticipants < currentParticipants) {
            throw new BadRequestException(ErrorResponse.MAX_PARTICIPANTS_BELOW_CURRENT);
        }
    }

    private void validateOwnershipOrAdmin(Event event, Integer userId) {
        boolean isOwner = event.getOrganizer().getId().equals(userId);
        if (isOwner) {
            return;
        }
        User user = getValidUserBy(userId);
        if (!ADMIN_ROLE.equalsIgnoreCase(user.getRole().getName())) {
            throw new ForbiddenException(ErrorResponse.NOT_EVENT_OWNER);
        }
    }

    private void validateUserId(Integer userId) {
        userValidationService.validateActiveUser(userId);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private EventDetailsResponseDto buildEventDetailsResponseDto(Event event, Integer userId) {
        EventDetailsResponseDto dto = eventMapper.toEventDetailsResponseDto(event);
        dto.setCurrentParticipants((int) registrationRepository.countByEventIdAndStatus(event.getId(), STATUS_GOING));
        dto.setSkillTags(event.getSkillTags().stream().map(SkillTag::getName).sorted().toList());
        contactRepository.findByUserId(event.getOrganizer().getId()).ifPresent(contact -> {
            dto.setOrganizerName(contact.getFullName());
            dto.setOrganizerEmail(contact.getEmail());
        });
        if (userId != null) {
            registrationRepository.findByUserIdAndEventId(userId, event.getId())
                    .ifPresent(reg -> dto.setUserRegistrationStatus(reg.getStatus()));
        }
        return dto;
    }

    private EventResponseDto toEventResponseDto(Event event) {
        EventResponseDto dto = eventMapper.toEventResponseDto(event);
        dto.setCurrentParticipants((int) registrationRepository.countByEventIdAndStatus(event.getId(), STATUS_GOING));
        dto.setSkillTags(event.getSkillTags().stream().map(SkillTag::getName).sorted().toList());
        dto.setSkillTagIds(event.getSkillTags().stream().map(SkillTag::getId).sorted().toList());
        dto.setIsCancelled(event.getIsCancelled());
        contactRepository.findByUserId(event.getOrganizer().getId())
                .ifPresent(contact -> dto.setOrganizerName(contact.getFullName()));
        return dto;
    }
}
