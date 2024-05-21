package pl.cezarysanecki.parkingdomain.commons.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

public interface DateProvider {

    LocalDateTime now();

    default Instant nowInstantUTC() {
        return now().toInstant(ZoneOffset.UTC);
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
