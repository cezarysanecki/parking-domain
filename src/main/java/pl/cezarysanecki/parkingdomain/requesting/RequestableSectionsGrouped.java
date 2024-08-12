package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.util.List;

record RequestableSectionsGrouped(
    List<RequestableSection> sections
) {

  static RequestableSectionsGrouped createNew(ParkingSpotId parkingSpotId, List<ParkingSpotSectionId> sections, TimeSlot timeSlot) {
    List<RequestableSection> requestableSections = sections.stream()
        .map(section -> RequestableSection.free(parkingSpotId, section, timeSlot))
        .toList();
    return new RequestableSectionsGrouped(requestableSections);
  }

  boolean requestBy(RequestId requestId) {
    return sections.stream()
        .allMatch(RequestableSection::isFree);
  }

}
