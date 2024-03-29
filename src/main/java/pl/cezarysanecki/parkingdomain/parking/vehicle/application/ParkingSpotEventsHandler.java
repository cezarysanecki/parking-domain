package pl.cezarysanecki.parkingdomain.parking.vehicle.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupationFailed;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotEventsHandler {

    private final DrivingVehicleAway drivingVehicleAway;

    @EventListener
    public void handle(ParkingSpotOccupationFailed parkingSpotOccupationFailed) {
        VehicleId vehicleId = parkingSpotOccupationFailed.getVehicleId();

        drivingVehicleAway.driveAway(new DrivingVehicleAway.Command(vehicleId));
    }

}
