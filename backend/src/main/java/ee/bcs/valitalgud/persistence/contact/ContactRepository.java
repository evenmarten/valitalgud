package ee.bcs.valitalgud.persistence.contact;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    Optional<Contact> findByEmailIgnoreCase(String email);

    Optional<Contact> findByUserId(Integer userId);
}
