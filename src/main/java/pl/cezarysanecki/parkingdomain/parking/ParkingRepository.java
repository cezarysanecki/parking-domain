package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.ReservationId;

import java.time.Instant;

interface ParkingRepository {

  void saveNew(ParkingSpotSectionsGrouped parkingSpotSectionsGrouped);

  ParkingSpotSectionsGrouped loadBy(ParkingSpotId parkingSpotId, Instant activationDateOfReservations);

  ParkingSpotSectionsGrouped loadBy(ReservationId reservationId, Instant activationDateOfReservations);

}
