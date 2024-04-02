package pl.cezarysanecki.parkingdomain.reservation.client.model;

import io.vavr.control.Option;

public interface ClientReservationsRepository {

    Option<ClientReservations> findBy(ClientId clientId);

    ClientReservations publish(ClientReservationsEvent clientReservationsEvent);

}
