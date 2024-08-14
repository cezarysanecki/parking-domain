package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.parking.api.ReservationId;

import java.util.List;

interface ReservationRepository {

  void saveAll(List<Reservation> reservations);

  Reservation loadBy(ReservationId reservationId);

}
