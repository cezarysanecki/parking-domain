package pl.cezarysanecki.parkingdomain.requestingreservation.model.template;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;

public record ParkingSpotReservationRequestsTemplate(
        ParkingSpotReservationRequestsTemplateId templateId,
        ParkingSpotId parkingSpotId,
        ParkingSpotCategory parkingSpotCategory,
        ParkingSpotCapacity capacity) {
}
