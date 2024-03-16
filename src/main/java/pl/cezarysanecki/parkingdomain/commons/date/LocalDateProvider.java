package pl.cezarysanecki.parkingdomain.commons.date;

import java.time.LocalDateTime;

class LocalDateProvider implements DateProvider {

    private LocalDateTime currentDateTime = LocalDateTime.now();

    @Override
    public LocalDateTime now() {
        return currentDateTime;
    }

    LocalDateTime passHours(int hours) {
        currentDateTime = currentDateTime.plusHours(hours);
        return currentDateTime;
    }

    LocalDateTime passMinutes(int minutes) {
        currentDateTime = currentDateTime.plusMinutes(minutes);
        return currentDateTime;
    }

}
