package ee.bcs.valitalgud.persistence.city;

import ee.bcs.valitalgud.controller.city.dto.CityResponseDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CityMapper {

    @Mapping(source = "county.id", target = "countyId")
    @Mapping(source = "county.name", target = "county")
    CityResponseDto toCityResponseDto(City city);

    List<CityResponseDto> toCityResponseDtos(List<City> cities);
}
