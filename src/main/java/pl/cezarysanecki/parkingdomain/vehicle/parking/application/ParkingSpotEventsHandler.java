package pl.cezarysanecki.parkingdomain.vehicle.parking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotOccupationFailed;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleId;

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
