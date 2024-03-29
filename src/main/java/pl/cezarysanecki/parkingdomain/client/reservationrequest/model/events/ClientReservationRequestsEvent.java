package pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events;

import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

public interface ClientReservationRequestsEvent extends DomainEvent {

    ClientId getClientId();

    ReservationId getReservationId();

}
