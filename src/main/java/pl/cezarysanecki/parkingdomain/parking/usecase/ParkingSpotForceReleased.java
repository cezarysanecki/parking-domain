package pl.cezarysanecki.parkingdomain.parking.usecase;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

public record ParkingSpotForceReleased(
    OccupationId occupationId,
    OccupantId occupantId,
    ParkingSpotId parkingSpotId,
    SpotUnits spotUnits,
    Reason reason
) implements DomainEvent {

  public enum Reason {
    NOT_RELEASED_PARKING_SPOT
  }

}
