package pl.cezarysanecki.parkingdomain.parking.model.occupation;

import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

public interface OccupationEvent extends DomainEvent {

  OccupationId occupationId();

  record OccupationReleased(
      @NonNull OccupationId occupationId,
      @NonNull ParkingSpotId parkingSpotId
  ) implements OccupationEvent {
  }

}
