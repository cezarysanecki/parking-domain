package pl.cezarysanecki.parkingdomain.parking.model.occupation;

import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

public interface OccupationEvent extends DomainEvent {

  OccupationId occupationId();

  record OccupationReleased(
      @NonNull OccupationId occupationId
  ) implements OccupationEvent {
  }

}
