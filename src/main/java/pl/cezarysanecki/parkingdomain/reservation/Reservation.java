package pl.cezarysanecki.parkingdomain.reservation;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservedSpace;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

@AllArgsConstructor
class Reservation implements ReservedSpace {

  final ReservationId reservationId;
  final ReservationOwnerId ownerId;
  final ParkingSpotId parkingSpotId;
  final TimeSlot timeSlot;
  final SpotUnits spotUnits;
  boolean used;

  @Override
  public ParkingSpotId parkingSpotId() {
    return parkingSpotId;
  }

  @Override
  public SpotUnits spotUnits() {
    return spotUnits;
  }
}
