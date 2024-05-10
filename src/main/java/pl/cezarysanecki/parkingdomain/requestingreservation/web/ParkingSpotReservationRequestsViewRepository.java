package pl.cezarysanecki.parkingdomain.requestingreservation.web;

import io.vavr.collection.List;

import java.util.UUID;

public interface ParkingSpotReservationRequestsViewRepository {

    List<CapacityView> queryForAllAvailableParkingSpots();

    record CapacityView(
            UUID parkingSpotId,
            int capacity,
            int spaceLeft
    ) {}

}
