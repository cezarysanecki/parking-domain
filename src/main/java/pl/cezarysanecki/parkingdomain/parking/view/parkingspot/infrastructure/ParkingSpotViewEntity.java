package pl.cezarysanecki.parkingdomain.parking.view.parkingspot.infrastructure;

import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
class ParkingSpotViewEntity {

    UUID parkingSpotId;
    Set<ParkedVehicleView> parkedVehicles;
    int capacity;

    @AllArgsConstructor
    static class ParkedVehicleView {

        UUID vehicleId;
        int size;

    }


}
