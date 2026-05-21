package ee.bcs.valitalgud.persistence.event;

import ee.bcs.valitalgud.controller.event.dto.CreateEventDto;
import ee.bcs.valitalgud.controller.event.dto.EventDetailsResponseDto;
import ee.bcs.valitalgud.controller.event.dto.EventResponseDto;
import ee.bcs.valitalgud.controller.event.dto.UpdateEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface EventMapper {

    @Mapping(source = "id", target = "eventId")
    @Mapping(source = "city.id", target = "cityId")
    @Mapping(source = "city.name", target = "city")
    @Mapping(source = "city.county.id", target = "countyId")
    @Mapping(source = "city.county.name", target = "county")
    @Mapping(source = "organizer.id", target = "organizerId")
    @Mapping(target = "organizerName", ignore = true)
    @Mapping(target = "skillTags", ignore = true)
    @Mapping(target = "skillTagIds", ignore = true)
    @Mapping(target = "currentParticipants", ignore = true)
    EventResponseDto toEventResponseDto(Event event);

    @Mapping(source = "id", target = "eventId")
    @Mapping(source = "city.name", target = "city")
    @Mapping(source = "city.county.name", target = "county")
    @Mapping(source = "organizer.id", target = "organizerId")
    @Mapping(target = "organizerName", ignore = true)
    @Mapping(target = "organizerEmail", ignore = true)
    @Mapping(target = "skillTags", ignore = true)
    @Mapping(target = "currentParticipants", ignore = true)
    @Mapping(target = "userRegistrationStatus", ignore = true)
    EventDetailsResponseDto toEventDetailsResponseDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizer", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "skillTags", ignore = true)
    @Mapping(target = "isCancelled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "date", target = "eventDate")
    Event toEvent(CreateEventDto createEventDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizer", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "skillTags", ignore = true)
    @Mapping(target = "isCancelled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "date", target = "eventDate")
    void updateEventFromDto(UpdateEventDto updateEventDto, @MappingTarget Event event);
}
