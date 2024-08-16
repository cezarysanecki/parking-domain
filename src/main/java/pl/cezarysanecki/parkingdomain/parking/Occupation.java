package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

record Occupation(
    OccupationId occupationId,
    Occupant occupant,
    ParkingSpot parkingSpot,
    SpotUnits occupiedUnits,
    ReservationId reservationId
) {
}
