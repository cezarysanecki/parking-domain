package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.Reservation;

public interface ParkingSpotEvent extends DomainEvent {

  ParkingSpotId parkingSpotId();

  record ParkingSpotOccupied(
      @NonNull ParkingSpotId parkingSpotId,
      @NonNull Occupation occupation
  ) implements ParkingSpotEvent {
  }

  record ParkingSpotReservationFulfilled(
      @NonNull ParkingSpotId parkingSpotId,
      @NonNull Reservation reservation
  ) implements ParkingSpotEvent {
  }

}