package pl.cezarysanecki.parkingdomain.requestingreservation.client.model;

import io.vavr.control.Option;

public interface ClientReservationRequestsRepository {

    Option<ClientReservationRequests> findBy(ClientId clientId);

    Option<ClientReservationRequests> findBy(ReservationId reservationId);

    ClientReservationRequests publish(ClientReservationRequestsEvent clientReservationRequestsEvent);

}
