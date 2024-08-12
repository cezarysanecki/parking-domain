package pl.cezarysanecki.parkingdomain.requesting.api;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.util.List;

public record MadeRequestsValid(
    List<Request> requests
) implements DomainEvent {

  public record Request(
      RequestId requestId,
      RequesterId requester,
      ParkingSpotId parkingSpotId,
      TimeSlot timeSlot,
      List<ParkingSpotSectionId> sections
  ) {
  }

}
