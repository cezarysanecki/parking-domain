package pl.cezarysanecki.parkingdomain.occupationreleasenotification;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

record NotificationResolver(
    Instant date,
    ParkingSpotId parkingSpotId,
    ParkingSpotCapacity capacity,
    List<Occupation> occupations,
    List<Reservation> reservations
) {

  List<OccupantToNotify> resolveOccupantsToNotify() {
    if (capacity.value() - occupiedSpace() - reservedSpace() >= 0) {
      return List.of();
    }
    int neededSpace = reservedSpace() - occupiedSpace();

    List<Occupation> sortedOccupationBySpaceUnits = occupations.stream()
        .sorted((o1, o2) -> o1.spotUnits.value() > o2.spotUnits.value() ? -1 : 1)
        .toList();

    List<OccupantToNotify> result = new ArrayList<>();
    for (Occupation occupation : sortedOccupationBySpaceUnits) {
      neededSpace -= occupation.spotUnits.value();

      if (isReservationOwner(occupation.occupantId)) {
        continue;
      }
      result.add(new OccupantToNotify(
          occupation.occupantId,
          parkingSpotId,
          date
      ));

      if (neededSpace <= 0) {
        return result;
      }
    }
    return result;
  }

  private boolean isReservationOwner(OccupantId occupantId) {
    return reservations.stream()
        .anyMatch(reservation -> reservation.reservationOwnerId.value().equals(occupantId.value()));
  }

  private int occupiedSpace() {
    return occupations.stream()
        .map(Occupation::spotUnits)
        .map(SpotUnits::value)
        .reduce(0, Integer::sum);
  }

  private int reservedSpace() {
    return reservations.stream()
        .map(Reservation::spotUnits)
        .map(SpotUnits::value)
        .reduce(0, Integer::sum);
  }

  record Occupation(
      OccupationId occupationId,
      OccupantId occupantId,
      SpotUnits spotUnits
  ) {
  }

  record Reservation(
      ReservationId reservationId,
      ReservationOwnerId reservationOwnerId,
      SpotUnits spotUnits
  ) {
  }

}
