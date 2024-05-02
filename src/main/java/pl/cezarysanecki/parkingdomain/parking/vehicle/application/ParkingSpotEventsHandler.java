package pl.cezarysanecki.parkingdomain.parking.vehicle.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.OccupationFailed;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ParkingSpotEventsHandler {

    private final DrivingVehicleAway drivingVehicleAway;

    @EventListener
    public void handle(OccupationFailed occupationFailed) {
        VehicleId vehicleId = occupationFailed.getVehicleId();

        drivingVehicleAway.driveAway(new DrivingVehicleAway.Command(vehicleId))
                .onFailure(exception -> log.error(exception.getMessage(), exception));
    }

}
