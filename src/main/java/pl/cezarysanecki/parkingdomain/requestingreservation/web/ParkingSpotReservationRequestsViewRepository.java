package pl.cezarysanecki.parkingdomain.requestingreservation.web;

import java.util.List;
import java.util.UUID;

public interface ParkingSpotReservationRequestsViewRepository {

    List<CapacityView> queryForAllAvailableParkingSpots();

    record CapacityView(
            UUID parkingSpotId,
            int capacity,
            int spaceLeft
    ) {}

}
