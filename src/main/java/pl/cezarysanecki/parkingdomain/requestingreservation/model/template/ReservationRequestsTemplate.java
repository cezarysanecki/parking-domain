package pl.cezarysanecki.parkingdomain.requestingreservation.model.template;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;

public record ReservationRequestsTemplate(
        ReservationRequestsTemplateId templateId,
        ParkingSpotId parkingSpotId,
        ParkingSpotCategory category,
        ParkingSpotCapacity capacity) {
}
