package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.city.dto.CityResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.persistence.city.City;
import ee.bcs.valitalgud.persistence.city.CityMapper;
import ee.bcs.valitalgud.persistence.city.CityRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    @Transactional(readOnly = true)
    public List<CityResponseDto> findCities() {
        List<City> cities = cityRepository.findAll();
        return cityMapper.toCityResponseDtos(cities);
    }

    public City getValidCityBy(Integer cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new BadRequestException(ErrorResponse.CITY_NOT_FOUND));
    }
}
