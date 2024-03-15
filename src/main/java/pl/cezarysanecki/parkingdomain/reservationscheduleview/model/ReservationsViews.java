package pl.cezarysanecki.parkingdomain.reservationscheduleview.model;

import pl.cezarysanecki.parkingdomain.client.model.ClientId;

public interface ReservationsViews {

    ReservationsView findFor(ClientId clientId);

}
