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
            JOIN cities c ON c.id = e.city_id
            LEFT JOIN event_skill_tags est ON est.event_id = e.id
            WHERE (CAST(:organizerId AS integer) IS NULL OR e.organizer_id = :organizerId)
              AND (:includeCancelled = true OR e.is_cancelled = false)
              AND (CAST(:cityId AS integer) IS NULL OR e.city_id = :cityId)
              AND (CAST(:countyId AS integer) IS NULL OR c.county_id = :countyId)
              AND (CAST(:skillTagId AS integer) IS NULL OR est.skill_tag_id = :skillTagId)
              AND (CAST(:fromDate AS date) IS NULL OR e.event_date >= CAST(:fromDate AS date))
              AND (CAST(:toDate AS date) IS NULL OR e.event_date <= CAST(:toDate AS date))
            ORDER BY e.event_date ASC
            """, nativeQuery = true)
    List<Event> findFilteredEvents(
            @Param("cityId") Integer cityId,
            @Param("countyId") Integer countyId,
            @Param("skillTagId") Integer skillTagId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("organizerId") Integer organizerId,
            @Param("includeCancelled") Boolean includeCancelled);

    @Query(value = """
            SELECT DISTINCT EXTRACT(DAY FROM event_date)::int
            FROM events
            WHERE EXTRACT(MONTH FROM event_date) = :month
              AND EXTRACT(YEAR FROM event_date) = :year
              AND is_cancelled = false
            ORDER BY 1
            """, nativeQuery = true)
    List<Integer> findEventDaysForMonth(@Param("month") int month, @Param("year") int year);

    @Query("FROM Event e WHERE e.eventDate = :date AND e.isCancelled = false ORDER BY e.startTime ASC")
    List<Event> findByEventDateAndNotCancelled(@Param("date") LocalDate date);
}
