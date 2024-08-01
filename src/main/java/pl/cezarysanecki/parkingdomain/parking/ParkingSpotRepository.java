package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.Occupant;

interface ParkingSpotRepository {

  void saveNew(ParkingSpotSectionsGrouped parkingSpotSectionsGrouped);

  void saveCheckingVersion(ParkingSpotSectionsGrouped parkingSpotSectionsGrouped);

  ParkingSpotSectionsGrouped loadFreeSectionsBy(ParkingSpotId parkingSpotId);

  ParkingSpotSectionsGrouped loadOccupiedSectionsBy(Occupant occupant);

  ParkingSpotSectionsGrouped loadBy(ParkingSpotId parkingSpotId);

}
