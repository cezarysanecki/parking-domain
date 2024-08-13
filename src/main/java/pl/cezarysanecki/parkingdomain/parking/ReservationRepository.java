package pl.cezarysanecki.parkingdomain.parking;

interface ReservationRepository {

  void saveAll(List<Reservation> reservations);

}
