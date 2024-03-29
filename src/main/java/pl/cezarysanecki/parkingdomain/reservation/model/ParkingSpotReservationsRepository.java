package pl.cezarysanecki.parkingdomain.reservation.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.model.events.ParkingSpotReservationsEvent;

public interface ParkingSpotReservationsRepository {

    void createFor(ParkingSpotId parkingSpotId, ParkingSpotType parkingSpotType, int capacity);

    Option<ParkingSpotReservations> findBy(ParkingSpotId parkingSpotId);

    Option<ParkingSpotReservations> findBy(ReservationId reservationId);

    Option<ParkingSpotReservations> findFor(ParkingSpotType parkingSpotType, VehicleSizeUnit vehicleSizeUnit);

    ParkingSpotReservations publish(ParkingSpotReservationsEvent event);

}
