package ee.bcs.valitalgud.persistence.skilltag;

import ee.bcs.valitalgud.controller.skilltag.dto.SkillTagResponseDto;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface SkillTagMapper {

    SkillTagResponseDto toSkillTagResponseDto(SkillTag skillTag);

    List<SkillTagResponseDto> toSkillTagResponseDtos(List<SkillTag> skillTags);
}
