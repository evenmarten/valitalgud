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
    @Mapping(target = "middleName", ignore = true)
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
        String[] parts = trimmed.split("\\s+");
        if (parts.length == 1) {
            dto.setFirstName(parts[0]);
            dto.setLastName("");
            return;
        }
        if (parts.length == 2) {
            dto.setFirstName(parts[0]);
            dto.setLastName(parts[1]);
            return;
        }
        dto.setFirstName(parts[0]);
        dto.setLastName(parts[parts.length - 1]);
        StringBuilder middle = new StringBuilder(parts[1]);
        for (int i = 2; i < parts.length - 1; i++) {
            middle.append(' ').append(parts[i]);
        }
        dto.setMiddleName(middle.toString());
    }
}
