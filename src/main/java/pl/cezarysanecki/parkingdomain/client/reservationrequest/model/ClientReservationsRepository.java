package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

public interface ClientReservationsRepository {

    ClientReservations findBy(ClientId clientId);

    ClientReservations publish(ClientReservationsEvent clientReservationsEvent);

}
