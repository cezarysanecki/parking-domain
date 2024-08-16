package pl.cezarysanecki.parkingdomain.reservation;

import pl.cezarysanecki.parkingdomain.reservation.api.Reservation;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;

import java.util.List;

interface ReservationRepository {

  void saveAll(List<Reservation> reservations);

  Reservation loadBy(ReservationId reservationId);

  void saveCheckingUsage(Reservation reservation);
}
