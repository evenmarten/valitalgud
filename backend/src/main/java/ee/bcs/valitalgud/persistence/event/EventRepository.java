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

    @Query(value = """
            SELECT DISTINCT
                e.id AS eventId,
                e.title AS title,
                e.event_date AS date,
                c.name AS city,
                e.is_cancelled AS isCancelled,
                e.max_participants AS maxParticipants,
                (SELECT COUNT(*) FROM registrations r
                 WHERE r.event_id = e.id AND r.status = 'LAHEB') AS currentParticipants
            FROM events e
            JOIN cities c ON c.id = e.city_id
            LEFT JOIN event_skill_tags est ON est.event_id = e.id
            LEFT JOIN skill_tags st ON st.id = est.skill_tag_id
            WHERE e.organizer_id = :userId
              AND (CAST(:city AS text) IS NULL OR c.name = :city)
              AND (CAST(:skillTag AS text) IS NULL OR st.name = :skillTag)
              AND (CAST(:date AS date) IS NULL OR e.event_date = CAST(:date AS date))
            ORDER BY e.event_date DESC
            """, nativeQuery = true)
    List<OrganizedEventProjection> findOrganizedEventsBy(
            @Param("userId") Integer userId,
            @Param("city") String city,
            @Param("skillTag") String skillTag,
            @Param("date") LocalDate date);

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
