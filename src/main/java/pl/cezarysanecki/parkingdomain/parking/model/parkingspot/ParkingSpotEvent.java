package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.Reservation;

public interface ParkingSpotEvent extends DomainEvent {

  ParkingSpotId parkingSpotId();

  Version parkingSpotVersion();

  record ParkingSpotOccupied(
      @NonNull ParkingSpotId parkingSpotId,
      @NonNull Occupation occupation,
      @NonNull Version parkingSpotVersion
  ) implements ParkingSpotEvent {
  }

  record ParkingSpotReservationFulfilled(
      @NonNull ParkingSpotId parkingSpotId,
      @NonNull Reservation reservation,
      @NonNull Version parkingSpotVersion
  ) implements ParkingSpotEvent {
  }

  record ParkingSpotPutIntoService(
      @NonNull ParkingSpotId parkingSpotId,
      @NonNull Version parkingSpotVersion
  ) implements ParkingSpotEvent {
  }

  record ParkingSpotMadeOutOfUse(
      @NonNull ParkingSpotId parkingSpotId,
      @NonNull Version parkingSpotVersion
  ) implements ParkingSpotEvent {
  }

}
