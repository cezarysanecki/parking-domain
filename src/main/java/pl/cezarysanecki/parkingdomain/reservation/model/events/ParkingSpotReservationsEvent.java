package pl.cezarysanecki.parkingdomain.reservation.model.events;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

public interface ParkingSpotReservationsEvent extends DomainEvent {

    ReservationId getReservationId();

    ParkingSpotId getParkingSpotId();

}
