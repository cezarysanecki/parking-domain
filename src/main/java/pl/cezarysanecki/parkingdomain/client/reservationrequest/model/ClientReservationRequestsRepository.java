package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.ClientReservationRequestsEvent;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

public interface ClientReservationRequestsRepository {

    Option<ClientReservationRequests> findBy(ClientId clientId);

    Option<ClientReservationRequests> findBy(ReservationId reservationId);

    ClientReservationRequests publish(ClientReservationRequestsEvent clientReservationRequestsEvent);

}
