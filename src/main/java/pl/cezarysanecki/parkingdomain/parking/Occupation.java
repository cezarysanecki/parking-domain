package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ReservationId;

record Occupation(
    OccupationId occupationId,
    Occupant occupant,
    ParkingSpotSectionsGrouped parkingSpotSectionsGrouped,
    ReservationId reservationId
) {
}
