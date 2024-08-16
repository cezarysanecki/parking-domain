package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

interface ParkingSpotReservationRepository {

  void storeFor(ParkingSpotId parkingSpotId, ReservationId reservationId, SpotUnits spotUnits);

  void remove(ReservationId reservationId);

}
