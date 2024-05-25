package pl.cezarysanecki.parkingdomain.cleaning.model;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

import java.util.List;

public interface CleaningRepository {

  void increaseCounterFor(ParkingSpotId parkingSpotId);

  void resetCountersFor(List<ParkingSpotId> parkingSpotIds);

  List<ParkingSpotId> getAllRecordsWithCounterAbove(int limit);

}
