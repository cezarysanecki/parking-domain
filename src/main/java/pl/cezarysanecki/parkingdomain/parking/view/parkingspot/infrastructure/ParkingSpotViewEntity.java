package pl.cezarysanecki.parkingdomain.parking.view.parkingspot.infrastructure;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCategory;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
class ParkingSpotViewEntity {

    UUID parkingSpotId;
    Set<ParkedVehicleView> parkedVehicles;
    int capacity;
    ParkingSpotCategory parkingSpotCategory;

    @AllArgsConstructor
    static class ParkedVehicleView {

        UUID vehicleId;
        int size;

    }


}
