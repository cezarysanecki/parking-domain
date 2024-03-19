package pl.cezarysanecki.parkingdomain.clientreservationsview.model;

import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

public interface ClientsReservationsViews {

    ClientReservationsView findFor(ClientId clientId);

    ClientReservationsView addReservation(ClientId clientId, ParkingSpotId parkingSpotId, ReservationId reservationId, ReservationSlot reservationSlot);

    ClientReservationsView removeReservation(ClientId clientId, ReservationId reservationIde);

}
