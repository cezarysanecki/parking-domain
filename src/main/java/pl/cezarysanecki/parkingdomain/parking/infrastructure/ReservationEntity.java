package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.Reservation;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.util.UUID;

@AllArgsConstructor
class ReservationEntity {

  UUID reservationId;
  UUID parkingSpotId;
  UUID beneficiaryId;
  int spotUnits;

  Reservation toDomain() {
    return new Reservation(
        ReservationId.of(reservationId),
        ParkingSpotId.of(parkingSpotId),
        BeneficiaryId.of(beneficiaryId),
        SpotUnits.of(spotUnits));
  }

}
