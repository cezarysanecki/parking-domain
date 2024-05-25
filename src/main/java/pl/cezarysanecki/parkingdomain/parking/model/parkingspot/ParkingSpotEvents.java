package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

public interface ParkingSpotEvents extends DomainEvent {

  ParkingSpotId parkingSpotId();

  record ParkingSpotOccupied(
      @NonNull ParkingSpotId parkingSpotId,
      @NonNull Occupation occupation
  ) implements ParkingSpotEvents {
  }

  record ParkingSpotReleased(
      @NonNull ParkingSpotId parkingSpotId,
      @NonNull Occupation occupation
  ) implements ParkingSpotEvents {
  }

}
