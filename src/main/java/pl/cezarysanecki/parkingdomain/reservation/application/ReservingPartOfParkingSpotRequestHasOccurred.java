package pl.cezarysanecki.parkingdomain.reservation.application;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod;

public interface ReservingPartOfParkingSpotRequestHasOccurred extends DomainEvent {

    ReservationId getReservationId();

    ReservationPeriod getReservationPeriod();

    VehicleSizeUnit getVehicleSizeUnit();

    ParkingSpotType getParkingSpotType();

}
