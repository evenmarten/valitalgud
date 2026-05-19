package ee.bcs.valitalgud.persistence.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Integer> {

    long countByEventIdAndStatus(Integer eventId, String status);
}
