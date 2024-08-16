package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.time.LocalDate;
import java.util.List;

interface RequestableParkingSpotRepository {

  void saveTemplate(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity);

  List<RequestableParkingSpotTemplate> findAllTemplates();

  void saveAllNewFor(List<RequestableParkingSpot> requestableParkingSpots);

  RequestableParkingSpot findFor(ParkingSpotId parkingSpotId, TimeSlot timeSlot);

  boolean intersects(ParkingSpotId parkingSpotId, TimeSlot timeSlot);

  void deleteAllFor(LocalDate day);

}
