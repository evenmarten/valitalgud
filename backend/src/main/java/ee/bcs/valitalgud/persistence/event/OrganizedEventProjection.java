package ee.bcs.valitalgud.persistence.event;

import java.time.LocalDate;

public interface OrganizedEventProjection {

    Integer getEventId();

    String getTitle();

    LocalDate getDate();

    String getCity();

    Boolean getIsCancelled();

    Long getCurrentParticipants();

    Integer getMaxParticipants();
}
