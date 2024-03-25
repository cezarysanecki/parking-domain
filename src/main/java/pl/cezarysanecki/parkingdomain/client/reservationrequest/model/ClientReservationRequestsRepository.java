package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

public interface ClientReservationRequestsRepository {

    ClientReservationRequests findBy(ClientId clientId);

    Option<ClientReservationRequests> findBy(ReservationId reservationId);

    ClientReservationRequests publish(ClientReservationRequestsEvent clientReservationRequestsEvent);

}
