package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.parking.model.FreeParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.parking.model.PartiallyOccupiedParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;

@Slf4j
@RequiredArgsConstructor
public class ParkingVehicle {

    private final ParkingSpots parkingSpots;

    public void park(ParkVehicleCommand command) {
        ParkingSpotId parkingSpotId = command.getParkingSpotId();
        VehicleId vehicleId = command.getVehicleId();

        parkingSpots.findBy(parkingSpotId)
                .map(parkingSpot -> switch (parkingSpot) {
                    case FreeParkingSpot freeParkingSpot -> freeParkingSpot.occupyBy(vehicleId);
                    case PartiallyOccupiedParkingSpot partiallyOccupiedParkingSpot ->
                            partiallyOccupiedParkingSpot.occupyBy(vehicleId);
                    default -> {
                        log.error("cannot find parking spot to park vehicle for id: " + parkingSpotId);
                        yield parkingSpot;
                    }
                })
                .ifPresent(parkingSpots::save);
    }

}
