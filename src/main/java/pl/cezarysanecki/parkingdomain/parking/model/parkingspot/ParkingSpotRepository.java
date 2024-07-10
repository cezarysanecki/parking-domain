package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import io.vavr.control.Option;
import io.vavr.control.Try;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingReservedParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

public interface ParkingSpotRepository {

  void saveNew(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity, ParkingSpotCategory category);

  Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId);

  Option<ParkingSpot> findBy(ReservationId reservationId);

  void publish(ParkingSpotEvent event);

  Option<ParkingSpot> findAvailableFor(ParkingSpotCategory category, SpotUnits spotUnits);

  default ParkingSpot getBy(ParkingSpotId parkingSpotId, OccupyingParkingSpot occupyingParkingSpot) {
    return findBy(parkingSpotId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot with id " + parkingSpotId));
  }

  default ParkingSpot getAvailableFor(ParkingSpotCategory category, SpotUnits spotUnits, OccupyingParkingSpot occupyingParkingSpot) {
    return findAvailableFor(category, spotUnits)
        .getOrElseThrow(() -> new IllegalStateException("cannot find any parking spot with category " + category + " and having enough space"));
  }

  default ParkingSpot getBy(ReservationId reservationId, OccupyingReservedParkingSpot occupyingReservedParkingSpot) {
    return findBy(reservationId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot containing reservation with id: " + reservationId));
  }
}
