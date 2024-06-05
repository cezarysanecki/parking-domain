package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.collection.HashMap;
import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.Reservation;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
class ParkingSpotEntity {

  UUID parkingSpotId;
  int capacity;
  int usedSpace;
  boolean outOfUse;
  ParkingSpotCategory category;
  int version;

  ParkingSpot toDomain(List<ReservationEntity> reservations) {
    return new ParkingSpot(
        ParkingSpotId.of(parkingSpotId),
        ParkingSpotCapacity.of(capacity),
        usedSpace,
        HashMap.ofAll(reservations.stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toMap(Reservation::getReservationId, reservation -> reservation))),
        outOfUse,
        new Version(version));
  }

  boolean isFull() {
    return capacity == usedSpace;
  }

  int spaceLeft() {
    return capacity - usedSpace;
  }

}
