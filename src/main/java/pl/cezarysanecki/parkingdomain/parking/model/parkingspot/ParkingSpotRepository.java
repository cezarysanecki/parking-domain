package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import io.vavr.control.Option;
import io.vavr.control.Try;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

public interface ParkingSpotRepository {

    void save(ParkingSpot parkingSpot);

    Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId);

    Option<ParkingSpot> findBy(OccupationId occupationId);

    Option<ParkingSpot> findBy(ReservationId reservationId);

    Option<ParkingSpot> findAvailableBy(ParkingSpotCategory parkingSpotCategory);
}
