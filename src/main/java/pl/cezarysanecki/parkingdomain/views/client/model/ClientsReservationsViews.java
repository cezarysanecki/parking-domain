package pl.cezarysanecki.parkingdomain.views.client.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

public interface ClientsReservationsViews {

    ClientReservationsView findFor(ClientId clientId);

    ClientReservationsView addPendingReservation(ClientId clientId, Option<ParkingSpotId> parkingSpotId, ReservationId reservationId);

    ClientReservationsView approveReservation(ClientId clientId, ParkingSpotId parkingSpotId, ReservationId reservationId);

    ClientReservationsView cancelReservation(ClientId clientId, ReservationId reservationIde);

}
