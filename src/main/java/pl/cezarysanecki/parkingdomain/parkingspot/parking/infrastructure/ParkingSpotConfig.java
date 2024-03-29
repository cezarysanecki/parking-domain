package pl.cezarysanecki.parkingdomain.parkingspot.parking.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.application.CreatingParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.application.VehicleDroveAwayEventHandler;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.application.VehicleParkedEventHandler;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpots;

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
