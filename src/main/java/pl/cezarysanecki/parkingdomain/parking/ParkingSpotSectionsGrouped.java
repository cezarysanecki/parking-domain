package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.parking.api.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.List;
import java.util.Optional;

record ParkingSpotSectionsGrouped(
    List<ParkingSpotSection> sections,
    List<Reservation> reservations
) {

  ParkingSpotSectionsGrouped {
    if (sections.isEmpty()) {
      throw new IllegalStateException("grouped sections cannot be empty");
    }
    if (sections.stream()
        .map(ParkingSpotSection::parkingSpotId)
        .distinct()
        .count() > 1) {
      throw new IllegalStateException("sections must be for the same parking spot");
    }
  }

  static ParkingSpotSectionsGrouped create(ParkingSpotId parkingSpotId, List<ParkingSpotSectionId> sections) {
    List<ParkingSpotSection> createdSections = sections.stream()
        .map(section -> ParkingSpotSection.free(parkingSpotId, section))
        .toList();
    return new ParkingSpotSectionsGrouped(createdSections, List.of());
  }

  boolean occupyBy(SpotUnits spotUnits) {
    int freeSpace = (int) sections.stream()
        .filter(ParkingSpotSection::isFree)
        .count();
    int reservedSpace = reservations.stream()
        .map(Reservation::spotUnits)
        .map(SpotUnits::value)
        .reduce(0, Integer::sum);
    return freeSpace - reservedSpace >= spotUnits.value();
  }

  boolean occupyUsing(ReservationId reservationId) {
    Optional<Reservation> currentReservation = reservations.stream()
        .filter(reservation -> reservation.reservationId().equals(reservationId))
        .findFirst();
    if (currentReservation.isEmpty()) {
      return false;
    }

    Reservation reservation = currentReservation.get();
    int freeSpace = (int) sections.stream()
        .filter(ParkingSpotSection::isFree)
        .count();
    return freeSpace >= reservation.spotUnits().value();
  }

  ParkingSpotId id() {
    return sections.getFirst().parkingSpotId();
  }

}
