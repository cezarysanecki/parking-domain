package pl.cezarysanecki.parkingdomain.commons.date;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Profile("!local")
@Component
class ProductionDateProvider implements DateProvider {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

}
