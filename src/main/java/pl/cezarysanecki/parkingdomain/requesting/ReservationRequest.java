package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.requesting.api.ReservationRequestId;

import java.util.List;

public record ReservationRequest(
    ReservationRequestId reservationRequestId,
    RequesterId requesterId,
    ParkingSpotId parkingSpotId,
    List<ParkingSpotSectionId> sectionIds
) {
}
