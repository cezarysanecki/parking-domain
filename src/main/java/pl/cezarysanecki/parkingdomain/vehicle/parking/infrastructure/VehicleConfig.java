package pl.cezarysanecki.parkingdomain.vehicle.parking.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.vehicle.parking.application.DrivingVehicleAway;
import pl.cezarysanecki.parkingdomain.vehicle.parking.application.ParkingSpotEventsHandler;
import pl.cezarysanecki.parkingdomain.vehicle.parking.application.ParkingVehicle;
import pl.cezarysanecki.parkingdomain.vehicle.parking.application.RegisteringVehicle;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.Vehicles;

@Configuration
@RequiredArgsConstructor
public class VehicleConfig {

    private final EventPublisher eventPublisher;

    @Bean
    RegisteringVehicle registeringVehicle(Vehicles vehicles) {
        return new RegisteringVehicle(vehicles);
    }

    @Bean
    ParkingVehicle parkingVehicle(Vehicles vehicles) {
        return new ParkingVehicle(vehicles);
    }

    @Bean
    DrivingVehicleAway drivingVehicleAway(Vehicles vehicles) {
        return new DrivingVehicleAway(vehicles);
    }

    @Bean
    ParkingSpotEventsHandler parkingSpotEventsHandler(Vehicles vehicles) {
        return new ParkingSpotEventsHandler(vehicles);
    }

    @Bean
    @Profile("local")
    Vehicles vehicles() {
        return new InMemoryVehicleRepository(eventPublisher);
    }

}
