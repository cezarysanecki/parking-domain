package pl.cezarysanecki.parkingdomain.parking;

import io.vavr.control.Try;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.catalogue.client.ClientId;
import pl.cezarysanecki.parkingdomain.catalogue.client.RegisteringClient;
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.AddingParkingSpot;
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.catalogue.vehicle.RegisteringVehicle;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure.ParkingSpotConfig;
import pl.cezarysanecki.parkingdomain.parking.vehicle.infrastructure.VehicleConfig;
import pl.cezarysanecki.parkingdomain.parking.view.parkingspot.infrastructure.ParkingSpotViewConfig;
import pl.cezarysanecki.parkingdomain.parking.view.vehicle.infrastructure.VehicleViewConfig;

import java.util.function.Supplier;

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
            RegisteringClient registeringClient,
            RegisteringVehicle registeringVehicle
    ) {
        return args -> {
            addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Bronze);
            addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Silver);
            addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Gold);

            ClientId clientId1 = mapClientId(() -> registeringClient.registerClient("789789789"));
            ClientId clientId2 = mapClientId(() -> registeringClient.registerClient("123456789"));
            ClientId clientId3 = mapClientId(() -> registeringClient.registerClient("456789123"));

            registeringVehicle.register(clientId1.getValue(), 1, "Honda", "Barossa 200");
            registeringVehicle.register(clientId1.getValue(), 2, "BMW", "RT");

            registeringVehicle.register(clientId2.getValue(), 1, "Peugeot", "Satellis");
            registeringVehicle.register(clientId2.getValue(), 2, "Honda", "CBR SC57B HRC");

            registeringVehicle.register(clientId3.getValue(), 1, "Yamacha", "X MAX");
            registeringVehicle.register(clientId3.getValue(), 4, "Skoda", "Fabia");
        };
    }

    private ClientId mapClientId(Supplier<Try<Result>> supplier) {
        return supplier.get()
                .filter(Result.Success.class::isInstance)
                .map(Result.Success.class::cast)
                .map(Result.Success::getResult)
                .filter(ClientId.class::isInstance)
                .map(ClientId.class::cast)
                .get();
    }

}

