package pl.cezarysanecki.parkingdomain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.management.client.RegisteringClient;
import pl.cezarysanecki.parkingdomain.management.parkingspot.AddingParkingSpot;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;

@Slf4j
@Configuration
class LocalConfig {

    @Profile("local")
    @Bean
    CommandLineRunner init(AddingParkingSpot addingParkingSpot, RegisteringClient registeringClient) {
        return args -> {
            addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Gold);
            addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Gold);
            addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Gold);

            registeringClient.registerClient("123123123");
            registeringClient.registerClient("321321321");
            registeringClient.registerClient("789789789");
        };
    }

}
