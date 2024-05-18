package pl.cezarysanecki.parkingdomain.commons.date;

import java.time.LocalDate;
import java.time.LocalDateTime;

class ProductionDateProvider implements DateProvider {

    @Override
    public LocalDate today() {
        return LocalDate.now();
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

}
