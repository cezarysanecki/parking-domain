package pl.cezarysanecki.parkingdomain;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.model.VehicleType;
import pl.cezarysanecki.parkingdomain.service.ParkingSpotService;
import pl.cezarysanecki.parkingdomain.service.VehicleService;

@SpringBootApplication
public class ParkingDomainApplication {

    public static void main(
            String[] args
    ) {
        SpringApplication.run(ParkingDomainApplication.class, args);
    }

    @Profile("local")
    @Component
    @RequiredArgsConstructor
    static class LocalLoader implements CommandLineRunner {

        private final ParkingSpotService parkingSpotService;
        private final VehicleService vehicleService;

        @Override
        public void run(final String... args) {
            parkingSpotService.create();
            parkingSpotService.create();
            parkingSpotService.create();
            parkingSpotService.create();

            vehicleService.create(VehicleType.CAR);
            vehicleService.create(VehicleType.CAR);
            vehicleService.create(VehicleType.MOTORCYCLE);
            vehicleService.create(VehicleType.MOTORCYCLE);
            vehicleService.create(VehicleType.MOTORCYCLE);
            vehicleService.create(VehicleType.BIKE);
            vehicleService.create(VehicleType.BIKE);
            vehicleService.create(VehicleType.BIKE);
            vehicleService.create(VehicleType.SCOOTER);
            vehicleService.create(VehicleType.SCOOTER);
        }

    }

}
