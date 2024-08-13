package pl.cezarysanecki.parkingdomain.parking;

import java.util.List;

interface ReservationRepository {

  void saveAll(List<Reservation> reservations);

}
