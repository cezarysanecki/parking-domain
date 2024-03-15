package pl.cezarysanecki.parkingdomain.clientreservationsview.model;

import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;

public interface ClientsReservationsView {

    ClientReservationsView findFor(ClientId clientId);

}
