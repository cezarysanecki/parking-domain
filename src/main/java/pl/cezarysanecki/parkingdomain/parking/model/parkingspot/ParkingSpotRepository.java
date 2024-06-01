package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;

public interface ParkingSpotRepository {

  void saveNew(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity, ParkingSpotCategory category);

  Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId);

  Option<ParkingSpot> findBy(OccupationId occupationId);

  Option<ParkingSpot> findBy(ReservationId reservationId);

  Option<ParkingSpot> findAvailableBy(ParkingSpotCategory parkingSpotCategory);

  void publish(ParkingSpotEvent event);

}
