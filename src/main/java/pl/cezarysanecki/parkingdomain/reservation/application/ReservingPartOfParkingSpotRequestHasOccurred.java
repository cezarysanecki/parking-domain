package pl.cezarysanecki.parkingdomain.reservation.application;

import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod;

public interface ReservingPartOfParkingSpotRequestHasOccurred {

    ReservationId getReservationId();

    ReservationPeriod getReservationPeriod();

    VehicleSizeUnit getVehicleSizeUnit();

    ParkingSpotType getParkingSpotType();

}
