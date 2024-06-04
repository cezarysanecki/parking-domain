package pl.cezarysanecki.parkingdomain.parking.model.reservation;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationEvent.ReservationAbandoned;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@Value
@AllArgsConstructor
public class Reservation {

  @NonNull
  ReservationId reservationId;
  @NonNull
  ParkingSpotId parkingSpotId;
  @NonNull
  BeneficiaryId beneficiaryId;
  @NonNull
  SpotUnits spotUnits;

  public Reservation(
      ParkingSpotId parkingSpotId,
      BeneficiaryId beneficiaryId,
      SpotUnits spotUnits
  ) {
    this.reservationId = ReservationId.newOne();
    this.parkingSpotId = parkingSpotId;
    this.beneficiaryId = beneficiaryId;
    this.spotUnits = spotUnits;
  }

  public ReservationAbandoned abandon() {
    return new ReservationAbandoned(reservationId);
  }

}
