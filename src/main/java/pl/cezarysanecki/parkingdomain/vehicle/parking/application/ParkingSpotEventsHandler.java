package pl.cezarysanecki.parkingdomain.vehicle.parking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotOccupationFailed;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.Vehicles;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotEventsHandler {

    private final Vehicles vehicles;

    @EventListener
    public void handle(ParkingSpotOccupationFailed parkingSpotOccupationFailed) {
        VehicleId vehicleId = parkingSpotOccupationFailed.getVehicleId();

        vehicles.findBy(vehicleId)
                .map(Vehicle::driveAway);
    }

}
