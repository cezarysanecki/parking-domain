package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.Beneficiary;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Reservation;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppendingReservation {

  public static Try<Reservation> append(
      ParkingSpot parkingSpot,
      Beneficiary beneficiary,
      ReservationRequest reservationRequest
  ) {
    Try<Reservation> parkingSpotReservationAppend = parkingSpot.reserveUsing(reservationRequest);
    if (parkingSpotReservationAppend.isFailure()) {
      return Try.failure(parkingSpotReservationAppend.getCause());
    }
    Reservation reservation = parkingSpotReservationAppend.get();

    Try<Reservation> beneficiaryReservationAppend = beneficiary.append(reservation);
    if (beneficiaryReservationAppend.isFailure()) {
      return Try.failure(beneficiaryReservationAppend.getCause());
    }

    return Try.of(() -> reservation);
  }

}
