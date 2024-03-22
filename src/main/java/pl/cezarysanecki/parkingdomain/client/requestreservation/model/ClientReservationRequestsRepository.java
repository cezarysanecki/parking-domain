package pl.cezarysanecki.parkingdomain.client.requestreservation.model;

public interface ClientReservationRequestsRepository {

    ClientReservationRequests findBy(ClientId clientId);

    ClientReservationRequests publish(ClientReservationRequestsEvent clientReservationRequestsEvent);

}
