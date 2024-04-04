package pl.cezarysanecki.parkingdomain.parking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.RegisteringVehicle;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;

@Slf4j
@Profile("local")
@Configuration
class ParkingLocalConfig {

    @Bean
    CommandLineRunner initParkingSpots(
            CreatingParkingSpot creatingParkingSpot,
            RegisteringVehicle registeringVehicle
    ) {
        return args -> {
            creatingParkingSpot.create(new CreatingParkingSpot.Command(ParkingSpotCapacity.of(4), ParkingSpotCategory.Bronze));
            creatingParkingSpot.create(new CreatingParkingSpot.Command(ParkingSpotCapacity.of(4), ParkingSpotCategory.Silver));
            creatingParkingSpot.create(new CreatingParkingSpot.Command(ParkingSpotCapacity.of(4), ParkingSpotCategory.Gold));

            registeringVehicle.register(new RegisteringVehicle.Command(VehicleSize.of(1)));
            registeringVehicle.register(new RegisteringVehicle.Command(VehicleSize.of(1)));
            registeringVehicle.register(new RegisteringVehicle.Command(VehicleSize.of(1)));
            registeringVehicle.register(new RegisteringVehicle.Command(VehicleSize.of(1)));
            registeringVehicle.register(new RegisteringVehicle.Command(VehicleSize.of(2)));
            registeringVehicle.register(new RegisteringVehicle.Command(VehicleSize.of(2)));
            registeringVehicle.register(new RegisteringVehicle.Command(VehicleSize.of(4)));
        };
    }

}
