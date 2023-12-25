package sunmisc.db.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface Live {

    LocalDateTime dateOfBirth();

    Optional<LocalDateTime> dateOfDeath();


    default boolean alive() {
        return dateOfDeath().isEmpty();
    }

    default int age() {
        return dateOfBirth()
                .toLocalDate()
                .until(LocalDate.now())
                .getYears();
    }
}
