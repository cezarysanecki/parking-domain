package pl.cezarysanecki.parkingdomain.commons.date;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
class ProductionDateProvider implements DateProvider {

    @Override
    public LocalDateTime currentLocalDateTime() {
        return LocalDateTime.now();
    }

}
