package ee.bcs.valitalgud.persistence.city;

import ee.bcs.valitalgud.controller.city.dto.CityResponseDto;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface CityMapper {

    CityResponseDto toCityResponseDto(City city);

    List<CityResponseDto> toCityResponseDtos(List<City> cities);
}
