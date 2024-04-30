package pl.cezarysanecki.parkingdomain.parking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.AddingParkingSpot;
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure.ParkingSpotConfig;
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.RegisteringVehicle;
import pl.cezarysanecki.parkingdomain.parking.vehicle.infrastructure.VehicleConfig;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.parking.view.parkingspot.infrastructure.ParkingSpotViewConfig;
import pl.cezarysanecki.parkingdomain.parking.view.vehicle.infrastructure.VehicleViewConfig;

@Configuration
@Import({
        ParkingSpotConfig.class,
        VehicleConfig.class,
        ParkingSpotViewConfig.class,
        VehicleViewConfig.class
})
public class ParkingConfig {

    @Bean
    @Profile("local")
    CommandLineRunner initParkingSpots(
            AddingParkingSpot addingParkingSpot,
            RegisteringVehicle registeringVehicle
    ) {
        return args -> {
            addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Bronze);
            addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Silver);
            addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Gold);

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

