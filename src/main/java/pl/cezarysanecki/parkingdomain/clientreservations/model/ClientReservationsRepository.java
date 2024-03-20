package pl.cezarysanecki.parkingdomain.clientreservations.model;

public interface ClientReservationsRepository {

    ClientReservations findBy(ClientId clientId);

    ClientReservations publish(ClientReservationsEvent clientReservationsEvent);

}
