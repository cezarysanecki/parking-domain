package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

record RequestableSection(
    ParkingSpotId parkingSpotId,
    ParkingSpotSectionId sectionId,
    TimeSlot timeSlot,
    RequestId requestId,
    Version version
) {

  static RequestableSection free(ParkingSpotId parkingSpotId, ParkingSpotSectionId sectionId, TimeSlot timeSlot) {
    return new RequestableSection(parkingSpotId, sectionId, timeSlot, RequestId.none(), Version.zero());
  }

  boolean isFree() {
    return requestId == null;
  }

}
