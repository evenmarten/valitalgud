package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.skilltag.dto.SkillTagResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.persistence.skilltag.SkillTag;
import ee.bcs.valitalgud.persistence.skilltag.SkillTagMapper;
import ee.bcs.valitalgud.persistence.skilltag.SkillTagRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillTagService {

    private final SkillTagRepository skillTagRepository;
    private final SkillTagMapper skillTagMapper;

    @Transactional(readOnly = true)
    public List<SkillTagResponseDto> findSkillTags() {
        List<SkillTag> skillTags = skillTagRepository.findAll();
        return skillTagMapper.toSkillTagResponseDtos(skillTags);
    }

    public Set<SkillTag> getValidSkillTagsBy(List<Integer> skillTagIds) {
        if (skillTagIds == null || skillTagIds.isEmpty()) {
            return new HashSet<>();
        }
        List<SkillTag> skillTags = skillTagRepository.findAllById(skillTagIds);
        if (skillTags.size() != skillTagIds.size()) {
            throw new BadRequestException(ErrorResponse.SKILL_TAG_NOT_FOUND);
        }
        return new HashSet<>(skillTags);
    }
}
