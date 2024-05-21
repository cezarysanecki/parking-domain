package pl.cezarysanecki.parkingdomain.commons.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface DateProvider {

    LocalDateTime now();

    default Instant nowInstant() {
        return Instant.from(now());
    }

    default LocalDate today() {
        return now().toLocalDate();
    }

    default LocalDate tomorrow() {
        return today().plusDays(1);
    }

    default LocalDateTime nearestFutureDateAt(LocalTime localTime) {
        LocalDateTime now = now();
        if (now.toLocalTime().isBefore(localTime)) {
            return LocalDateTime.of(now.toLocalDate(), localTime);
        }
        return LocalDateTime.of(now.toLocalDate().plusDays(1), localTime);
    }

}
