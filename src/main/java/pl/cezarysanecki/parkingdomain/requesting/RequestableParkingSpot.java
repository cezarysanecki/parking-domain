package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

record RequestableParkingSpot(
    ParkingSpotId parkingSpotId,
    int capacity,
    int occupiedSpots,
    TimeSlot timeSlot,
    Version version
) {

  static RequestableParkingSpot createNew(ParkingSpotId parkingSpotId, int capacity, TimeSlot timeSlot) {
    return new RequestableParkingSpot(parkingSpotId, capacity, 0, timeSlot, Version.zero());
  }

  boolean requestFor(SpotUnits spotUnits) {
    return (capacity - occupiedSpots) >= spotUnits.value();
  }

}
