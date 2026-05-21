package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.county.dto.CountyResponseDto;
import ee.bcs.valitalgud.persistence.county.County;
import ee.bcs.valitalgud.persistence.county.CountyMapper;
import ee.bcs.valitalgud.persistence.county.CountyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CountyService {

    private final CountyRepository countyRepository;
    private final CountyMapper countyMapper;

    @Transactional(readOnly = true)
    public List<CountyResponseDto> findCounties() {
        List<County> counties = countyRepository.findAll();
        return countyMapper.toCountyResponseDtos(counties);
    }
}
