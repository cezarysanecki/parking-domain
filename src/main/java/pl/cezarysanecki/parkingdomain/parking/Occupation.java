package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.Occupant;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;

import java.util.List;

record Occupation(
    OccupationId occupationId,
    Occupant occupant,
    ParkingSpotId parkingSpotId,
    List<ParkingSpotSection> sections
) {
}
