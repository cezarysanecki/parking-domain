package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

interface ParkingSpotRepository {

  void saveNew(ParkingSpotSectionsGrouped parkingSpotSectionsGrouped);

  ParkingSpotSectionsGrouped loadFreeSectionsFor(ParkingSpotId parkingSpotId, SpotUnits spotUnits);

  ParkingSpotSectionsGrouped loadBy(ParkingSpotId parkingSpotId);

}
