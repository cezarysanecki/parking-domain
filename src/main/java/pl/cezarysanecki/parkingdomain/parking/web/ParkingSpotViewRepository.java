package pl.cezarysanecki.parkingdomain.parking.web;

import java.util.List;
import java.util.UUID;

public interface ParkingSpotViewRepository {

    List<CapacityView> queryForAllAvailableParkingSpots();

    record CapacityView(
            UUID parkingSpotId,
            int capacity,
            int spaceLeft
    ) {}

}
