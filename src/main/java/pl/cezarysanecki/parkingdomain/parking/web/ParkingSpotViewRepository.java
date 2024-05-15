package pl.cezarysanecki.parkingdomain.parking.web;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;

import java.util.List;
import java.util.UUID;

public interface ParkingSpotViewRepository {

    List<CapacityView> queryForAllAvailableParkingSpots();

    record CapacityView(
            UUID parkingSpotId,
            ParkingSpotCategory category,
            int capacity,
            int spaceLeft
    ) {
    }

}
