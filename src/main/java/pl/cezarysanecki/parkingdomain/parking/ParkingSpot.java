package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.List;
import java.util.Optional;

record ParkingSpot(
    ParkingSpotId parkingSpotId,
    int occupiedSpace,
    List<Reservation> reservations,
    ParkingSpotCapacity capacity,
    Version version
) {

  static ParkingSpot create(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity) {
    return new ParkingSpot(parkingSpotId, 0, List.of(), capacity, Version.zero());
  }

  Optional<Reservation> occupyBy(ReservationId reservationId) {
    return reservations.stream()
        .filter(reservation -> reservation.reservationId().equals(reservationId)
            && occupiedSpace + reservation.spotUnits().value() <= capacity.value())
        .findFirst();
  }

  boolean occupyBy(SpotUnits spotUnits) {
    return (capacity.value() - occupiedSpace - reservedSpace()) >= spotUnits.value();
  }

  private int reservedSpace() {
    return reservations.stream()
        .map(Reservation::spotUnits)
        .map(SpotUnits::value)
        .reduce(0, Integer::sum);
  }

}
