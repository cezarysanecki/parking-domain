package pl.cezarysanecki.parkingdomain.client.view.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

public interface ClientsReservationsViews {

    ClientReservationsView findFor(ClientId clientId);

    ClientReservationsView addPendingReservation(ClientId clientId, Option<ParkingSpotId> parkingSpotId, ReservationId reservationId);

    Option<ClientReservationsView> approveReservation(ReservationId reservationId, ParkingSpotId parkingSpotId);

    Option<ClientReservationsView> cancelReservation(ReservationId reservationId);

}
