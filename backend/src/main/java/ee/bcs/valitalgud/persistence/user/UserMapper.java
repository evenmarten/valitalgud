package ee.bcs.valitalgud.persistence.user;

import ee.bcs.valitalgud.controller.login.dto.LoginResponseDto;
import ee.bcs.valitalgud.persistence.contact.Contact;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface UserMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.role.name", target = "role")
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    LoginResponseDto toLoginResponseDto(User user, Contact contact);

    @AfterMapping
    default void splitFullName(Contact contact, @MappingTarget LoginResponseDto dto) {
        String fullName = contact.getFullName();
        String trimmed = fullName == null ? "" : fullName.trim();
        if (trimmed.isEmpty()) {
            dto.setFirstName("");
            dto.setLastName("");
            return;
        }
        int firstSpace = trimmed.indexOf(' ');
        if (firstSpace < 0) {
            dto.setFirstName(trimmed);
            dto.setLastName("");
            return;
        }
        dto.setFirstName(trimmed.substring(0, firstSpace));
        dto.setLastName(trimmed.substring(firstSpace + 1).trim());
    }
}
