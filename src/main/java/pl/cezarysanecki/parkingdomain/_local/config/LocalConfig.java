package pl.cezarysanecki.parkingdomain._local.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.management.client.ClientType;
import pl.cezarysanecki.parkingdomain.management.client.RegisteringClient;
import pl.cezarysanecki.parkingdomain.management.parkingspot.AddingParkingSpot;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;

@Slf4j
@Configuration
@Profile("local")
public class LocalConfig {

    @Bean
    DateProvider dateProvider() {
        return new LocalDateProvider();
    }

    @Bean
    CommandLineRunner init(AddingParkingSpot addingParkingSpot, RegisteringClient registeringClient) {
        return args -> {
            addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Gold);
            addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Silver);
            addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Bronze);

            registeringClient.registerClient(ClientType.INDIVIDUAL, "123123123");
            registeringClient.registerClient(ClientType.INDIVIDUAL, "321321321");
            registeringClient.registerClient(ClientType.BUSINESS, "789789789");
        };
    }

}
