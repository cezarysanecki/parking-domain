package pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.application.VehicleDroveAwayEventHandler;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.application.VehicleParkedEventHandler;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotConfig {

    private final EventPublisher eventPublisher;

    @Bean
    CreatingParkingSpot creatingParkingSpot(ParkingSpots parkingSpots) {
        return new CreatingParkingSpot(parkingSpots);
    }

    @Bean
    VehicleDroveAwayEventHandler vehicleDroveAwayEventHandler(ParkingSpots parkingSpots) {
        return new VehicleDroveAwayEventHandler(parkingSpots);
    }

    @Bean
    VehicleParkedEventHandler vehicleParkedEventHandler(ParkingSpots parkingSpots) {
        return new VehicleParkedEventHandler(parkingSpots);
    }

    @Bean
    @Profile("local")
    ParkingSpots parkingSpots() {
        return new InMemoryParkingSpotRepository(eventPublisher);
    }

}
