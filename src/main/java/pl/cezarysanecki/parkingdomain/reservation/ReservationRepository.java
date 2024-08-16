package pl.cezarysanecki.parkingdomain.reservation;

import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;

import java.time.Instant;
import java.util.List;

interface ReservationRepository {

  void saveAll(List<Reservation> reservations);

  Reservation loadActiveBy(ReservationId reservationId);

  List<Reservation> loadAllStaleSince(Instant date);

  List<Reservation> loadAllActiveBy(Instant date);

  void markAsUsed(Reservation reservation);

  void markAsActive(List<ReservationId> reservations);

  void markAsNotUused(List<ReservationId> reservations);

}
