package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.util.List;

interface RequestableParkingSpotRepository {

  void saveTemplate(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity);

  List<RequestableParkingSpotTemplate> findAllTemplates();

  void saveNew(RequestableParkingSpot requestableParkingSpot);

  RequestableParkingSpot findFor(ParkingSpotId parkingSpotId, TimeSlot timeSlot);

  boolean intersects(ParkingSpotId parkingSpotId, TimeSlot timeSlot);

}
