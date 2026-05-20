package ee.bcs.valitalgud.persistence.registration;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Integer> {

    long countByEventIdAndStatus(Integer eventId, String status);

    Optional<Registration> findByUserIdAndEventId(Integer userId, Integer eventId);

    void deleteByUserIdAndEventId(Integer userId, Integer eventId);

    @Query(value = """
            SELECT DISTINCT e.id AS eventId,
                   e.title AS title,
                   e.event_date AS date,
                   c.name AS location,
                   e.description AS description,
                   r.status AS userRegistrationStatus
            FROM registrations r
            JOIN events e ON e.id = r.event_id
            JOIN cities c ON c.id = e.city_id
            LEFT JOIN event_skill_tags est ON est.event_id = e.id
            WHERE r.user_id = :userId
              AND e.is_cancelled = false
              AND e.event_date >= CAST(:fromDate AS date)
              AND (CAST(:toDate AS date) IS NULL OR e.event_date <= CAST(:toDate AS date))
              AND (CAST(:cityId AS integer) IS NULL OR e.city_id = :cityId)
              AND (CAST(:skillTagId AS integer) IS NULL OR est.skill_tag_id = :skillTagId)
              AND (CAST(:filterFromDate AS date) IS NULL OR e.event_date >= CAST(:filterFromDate AS date))
            ORDER BY e.event_date ASC
            """, nativeQuery = true)
    List<MyEventProjection> findMyEventsBy(
            @Param("userId") Integer userId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("cityId") Integer cityId,
            @Param("skillTagId") Integer skillTagId,
            @Param("filterFromDate") LocalDate filterFromDate);
}
