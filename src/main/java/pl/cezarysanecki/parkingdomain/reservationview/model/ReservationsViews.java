package pl.cezarysanecki.parkingdomain.reservationview.model;

import pl.cezarysanecki.parkingdomain.reservation.model.ClientId;

public interface ReservationsViews {

    ReservationsView findFor(ClientId clientId);

}
