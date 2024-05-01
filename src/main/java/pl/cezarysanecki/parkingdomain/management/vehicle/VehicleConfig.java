package pl.cezarysanecki.parkingdomain.management.vehicle;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Configuration
@RequiredArgsConstructor
public class VehicleConfig {

    private final EventPublisher eventPublisher;

    @Bean
    RegisteringVehicle registeringVehicle(CatalogueVehicleDatabase catalogueVehicleDatabase) {
        return new RegisteringVehicle(catalogueVehicleDatabase, eventPublisher);
    }

    @Bean
    @Profile("local")
    CatalogueVehicleDatabase catalogueVehicleDatabase() {
        return new CatalogueVehicleDatabase.InMemoryCatalogueVehicleDatabase();
    }

}
