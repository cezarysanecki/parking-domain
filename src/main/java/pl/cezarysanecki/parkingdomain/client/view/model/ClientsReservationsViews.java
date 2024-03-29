package pl.cezarysanecki.parkingdomain.client.view.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

public interface ClientsReservationsViews {

    ClientReservationsView findFor(ClientId clientId);

    void addPendingReservation(ClientId clientId, Option<ParkingSpotId> parkingSpotId, ReservationId reservationId);

    void approveReservation(ReservationId reservationId, ParkingSpotId parkingSpotId);

    void cancelReservation(ClientId clientId, ReservationId reservationIde);

    void rejectReservation(ClientId clientId, ReservationId reservationIde);

}
