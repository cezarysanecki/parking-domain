package pl.cezarysanecki.parkingdomain._local.config;

import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;

import java.time.LocalDateTime;

public class LocalDateProvider implements DateProvider {

    private LocalDateTime currentDateTime = LocalDateTime.now();

    @Override
    public LocalDateTime now() {
        return currentDateTime;
    }

    public LocalDateTime setCurrentDate(LocalDateTime localDateTime) {
        currentDateTime = localDateTime;
        return currentDateTime;
    }

    public LocalDateTime passHours(int hours) {
        currentDateTime = currentDateTime.plusHours(hours);
        return currentDateTime;
    }

    public LocalDateTime passMinutes(int minutes) {
        currentDateTime = currentDateTime.plusMinutes(minutes);
        return currentDateTime;
    }

}
