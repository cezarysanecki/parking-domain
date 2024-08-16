package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

interface ParkingRepository {

  void saveNew(ParkingSpot parkingSpot);

  ParkingSpot loadBy(ParkingSpotId parkingSpotId);

}
