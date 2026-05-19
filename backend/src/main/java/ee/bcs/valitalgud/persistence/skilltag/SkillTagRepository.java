package ee.bcs.valitalgud.persistence.skilltag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillTagRepository extends JpaRepository<SkillTag, Integer> {
}
