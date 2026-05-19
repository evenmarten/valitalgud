package ee.bcs.valitalgud.persistence.event;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query(value = """
            SELECT DISTINCT e.* FROM events e
            LEFT JOIN event_skill_tags est ON est.event_id = e.id
            WHERE e.is_cancelled = false
              AND (CAST(:cityId AS integer) IS NULL OR e.city_id = :cityId)
              AND (CAST(:skillTagId AS integer) IS NULL OR est.skill_tag_id = :skillTagId)
              AND (CAST(:fromDate AS date) IS NULL OR e.event_date >= CAST(:fromDate AS date))
            ORDER BY e.event_date ASC
            """, nativeQuery = true)
    List<Event> findFilteredEvents(
            @Param("cityId") Integer cityId,
            @Param("skillTagId") Integer skillTagId,
            @Param("fromDate") LocalDate fromDate);
}
