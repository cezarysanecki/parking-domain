package pl.cezarysanecki.parkingdomain.parking.view.vehicle.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.parking.view.vehicle.model.VehicleViews;

@Configuration
@RequiredArgsConstructor
public class VehicleViewConfig {

    @Bean
    @Profile("local")
    VehicleViews vehicleViews() {
        return new InMemoryVehicleViewRepository();
    }

}
