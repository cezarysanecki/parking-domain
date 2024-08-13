package pl.cezarysanecki.parkingdomain.cleaning;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

import java.util.List;

interface CleaningRepository {

  void increaseCounterFor(ParkingSpotId parkingSpotId);

  void resetAll();

  List<ParkingSpotId> getAllRecordsWithCounterAbove(int limit);

}
