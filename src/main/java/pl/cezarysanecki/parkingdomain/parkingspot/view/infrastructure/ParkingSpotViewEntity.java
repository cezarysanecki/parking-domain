package pl.cezarysanecki.parkingdomain.parkingspot.view.infrastructure;

import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
class ParkingSpotViewEntity {

    UUID parkingSpotId;
    Set<ParkedVehicleView> parkedVehicles;
    int capacity;

    @Value
    static class ParkedVehicleView {

        UUID vehicleId;
        int size;

    }


}
