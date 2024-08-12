package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

interface ParkingSpotRepository {

  void saveNew(ParkingSpotSectionsGrouped parkingSpotSectionsGrouped);

  ParkingSpotSectionsGrouped loadFreeSectionsFor(ParkingSpotId parkingSpotId, SpotUnits spotUnits);

  ParkingSpotSectionsGrouped loadBy(ParkingSpotId parkingSpotId);

}
