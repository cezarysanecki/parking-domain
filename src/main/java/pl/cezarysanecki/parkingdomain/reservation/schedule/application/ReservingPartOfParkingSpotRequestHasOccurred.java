package pl.cezarysanecki.parkingdomain.reservation.schedule.application;

import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationPeriod;

public interface ReservingPartOfParkingSpotRequestHasOccurred {

    ReservationId getReservationId();

    ReservationPeriod getReservationPeriod();

    VehicleSizeUnit getVehicleSizeUnit();

    ParkingSpotType getParkingSpotType();

}
