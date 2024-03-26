package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

public interface ParkingSpots {

    Option<ParkingSpot> findBy(ParkingSpotType parkingSpotType, VehicleSizeUnit vehicleSizeUnit);

    Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId);

    Option<ParkingSpot> findBy(ReservationId reservationId);

    Option<ParkingSpot> findBy(VehicleId vehicleId);

    ParkingSpot publish(ParkingSpotEvent event);

}
