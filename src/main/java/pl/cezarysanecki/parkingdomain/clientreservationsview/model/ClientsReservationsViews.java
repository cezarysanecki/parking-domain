package pl.cezarysanecki.parkingdomain.clientreservationsview.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

public interface ClientsReservationsViews {

    ClientReservationsView findFor(ClientId clientId);

    ClientReservationsView addPendingReservation(ClientId clientId, Option<ParkingSpotId> parkingSpotId, ReservationId reservationId, ReservationSlot reservationSlot);

    ClientReservationsView approveReservation(ClientId clientId, ParkingSpotId parkingSpotId, ReservationId reservationId);

    ClientReservationsView cancelReservation(ClientId clientId, ReservationId reservationIde);

}
