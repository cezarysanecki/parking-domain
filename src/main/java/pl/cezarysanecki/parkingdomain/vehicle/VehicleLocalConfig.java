package pl.cezarysanecki.parkingdomain.vehicle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.vehicle.parking.application.RegisteringVehicle;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleSize;

@Slf4j
@Profile("local")
@Configuration
class VehicleLocalConfig {

    @Bean
    CommandLineRunner init(RegisteringVehicle registeringVehicle) {
        return args -> {
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
