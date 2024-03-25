package pl.cezarysanecki.parkingdomain.reservation.schedule.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

public interface ParkingSpotReservationsRepository {

    void createFor(ParkingSpotId parkingSpotId, ParkingSpotType parkingSpotType, int capacity);

    Option<ParkingSpotReservations> findBy(ParkingSpotId parkingSpotId);

    Option<ParkingSpotReservations> findFor(ParkingSpotType parkingSpotType, VehicleSizeUnit vehicleSizeUnit);

    Option<ReservationId> deleteBy(ReservationId reservationId);

    ParkingSpotReservations publish(ParkingSpotReservationsEvent event);

}
