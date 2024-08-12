package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.util.List;

interface RequestableSectionRepository {

  void saveTemplate(ParkingSpotId parkingSpotId, List<ParkingSpotSectionId> sections);

  RequestableParkingSpotTemplate findTemplateBy(ParkingSpotId parkingSpotId);

  void saveNew(RequestableSectionsGrouped requestableSectionsGrouped);

  RequestableSectionsGrouped findFreeSectionsFor(ParkingSpotId parkingSpotId, TimeSlot timeSlot, SpotUnits spotUnits);

  RequestableSectionsGrouped loadBy(ParkingSpotId parkingSpotId, TimeSlot timeSlot);

  boolean intersects(ParkingSpotId parkingSpotId, TimeSlot timeSlot);

}
