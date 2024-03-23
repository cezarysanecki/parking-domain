package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

public interface ClientReservationRequestsRepository {

    ClientReservationRequests findBy(ClientId clientId);

    ClientReservationRequests publish(ClientReservationRequestsEvent clientReservationRequestsEvent);

}
