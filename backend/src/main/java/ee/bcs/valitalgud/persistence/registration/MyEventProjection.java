package ee.bcs.valitalgud.persistence.registration;

import java.time.LocalDate;

public interface MyEventProjection {

    Integer getEventId();

    String getTitle();

    LocalDate getDate();

    String getLocation();

    String getCounty();

    String getDescription();

    String getUserRegistrationStatus();
}
