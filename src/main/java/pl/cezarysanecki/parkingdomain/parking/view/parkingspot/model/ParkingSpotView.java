package pl.cezarysanecki.parkingdomain.parking.view.parkingspot.model;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotCategory;

import java.util.Set;
import java.util.UUID;

@Value
public class ParkingSpotView {

    UUID parkingSpotId;
    Set<ParkedVehicleView> parkedVehicles;
    int spaceLeft;
    int capacity;
    ParkingSpotCategory parkingSpotCategory;

    @Value
    public static class ParkedVehicleView {

        UUID vehicleId;

    }


}
