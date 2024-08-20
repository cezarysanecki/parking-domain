package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.List;

interface ActiveReservationRepository {

  void storeFor(ParkingSpotId parkingSpotId, ReservationId reservationId, ReservationOwnerId reservationOwnerId, SpotUnits spotUnits);

  void remove(List<ReservationId> reservations);

}
