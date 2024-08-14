package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.ReservationId;

interface ParkingRepository {

  void saveNew(ParkingSpotSectionsGrouped parkingSpotSectionsGrouped);

  ParkingSpotSectionsGrouped loadBy(ParkingSpotId parkingSpotId);

  ParkingSpotSectionsGrouped loadBy(ReservationId reservationId);

}
