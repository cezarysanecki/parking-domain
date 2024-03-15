package pl.cezarysanecki.parkingdomain.clientreservations.model;

import io.vavr.control.Option;

public interface ClientReservationsRepository {

    Option<ClientReservations> findBy(ClientId clientId);

    ClientReservations publish(ClientReservationsEvent clientReservationsEvent);

}
