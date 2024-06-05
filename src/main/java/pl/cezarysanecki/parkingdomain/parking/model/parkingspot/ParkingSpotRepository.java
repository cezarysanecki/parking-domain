package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import io.vavr.control.Option;
import io.vavr.control.Try;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

public interface ParkingSpotRepository {

  void saveNew(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity, ParkingSpotCategory category);

  Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId);

  Option<ParkingSpot> findBy(ReservationId reservationId);

  void publish(ParkingSpotEvent event);

  Option<ParkingSpot> findAvailableFor(ParkingSpotCategory category, SpotUnits spotUnits);

}
