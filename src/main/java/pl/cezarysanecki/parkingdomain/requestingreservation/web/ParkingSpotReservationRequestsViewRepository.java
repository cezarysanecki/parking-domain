package pl.cezarysanecki.parkingdomain.requestingreservation.web;

import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.util.List;
import java.util.UUID;

public interface ParkingSpotReservationRequestsViewRepository {

    List<ParkingSpotReservationRequestsView> queryForAllAvailableParkingSpots();

    record ParkingSpotReservationRequestsView(
            UUID parkingSpotId,
            UUID parkingSpotTimeSlotId,
            TimeSlot timeSlot,
            int capacity,
            int spaceLeft
    ) {
    }

}
