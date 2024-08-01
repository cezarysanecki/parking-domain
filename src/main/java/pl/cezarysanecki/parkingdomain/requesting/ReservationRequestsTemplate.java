package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;

public record ReservationRequestsTemplate(
    ReservationRequestsTemplateId templateId,
    ParkingSpotId parkingSpotId,
    ParkingSpotCategory category,
    ParkingSpotCapacity capacity) {
}
