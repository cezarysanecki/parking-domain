package pl.cezarysanecki.parkingdomain.requestingreservation.web;

import io.vavr.collection.List;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.util.UUID;

public interface ParkingSpotReservationRequestsViewRepository {

    List<CapacityView> queryForAllAvailableParkingSpots();

    record CapacityView(
            UUID parkingSpotId,
            TimeSlot timeSlot,
            int capacity,
            int spaceLeft
    ) {
    }

}
