package ee.bcs.valitalgud.persistence.event;

import ee.bcs.valitalgud.controller.event.dto.EventResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EventMapper {

    @Mapping(source = "id", target = "eventId")
    @Mapping(source = "city.id", target = "cityId")
    @Mapping(source = "city.name", target = "city")
    @Mapping(source = "organizer.id", target = "organizerId")
    @Mapping(target = "organizerName", ignore = true)
    @Mapping(target = "skillTags", ignore = true)
    @Mapping(target = "skillTagIds", ignore = true)
    @Mapping(target = "currentParticipants", ignore = true)
    EventResponseDto toEventResponseDto(Event event);
}
