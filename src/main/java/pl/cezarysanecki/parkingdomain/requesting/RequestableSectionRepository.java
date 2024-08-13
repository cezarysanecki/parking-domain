package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.util.List;

interface RequestableSectionRepository {

  void saveTemplate(ParkingSpotId parkingSpotId, List<ParkingSpotSectionId> sections);

  List<RequestableParkingSpotTemplate> findAllTemplates();

  void saveNew(RequestableSectionsGrouped requestableSectionsGrouped);

  RequestableSectionsGrouped findFreeSectionsFor(ParkingSpotId parkingSpotId, TimeSlot timeSlot, SpotUnits spotUnits);

  RequestableSectionsGrouped loadBy(ParkingSpotId parkingSpotId, TimeSlot timeSlot);

  boolean intersects(ParkingSpotId parkingSpotId, TimeSlot timeSlot);

}
