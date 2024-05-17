package pl.cezarysanecki.parkingdomain.cleaning.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cezarysanecki.parkingdomain.cleaning.application.RequestingCleaning;
import pl.cezarysanecki.parkingdomain.parking.application.ProvidingUsageOfParkingSpot;

@Configuration
@RequiredArgsConstructor
public class CleaningConfig {

    @Bean
    RequestingCleaning requestingCleaning(
            ProvidingUsageOfParkingSpot providingUsageOfParkingSpot
    ) {
        return new RequestingCleaning(providingUsageOfParkingSpot);
    }

}
