package ee.bcs.valitalgud.persistence.county;

import ee.bcs.valitalgud.controller.county.dto.CountyResponseDto;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface CountyMapper {

    CountyResponseDto toCountyResponseDto(County county);

    List<CountyResponseDto> toCountyResponseDtos(List<County> counties);
}