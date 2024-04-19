package pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.model;

import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ReservationRequestForPartOfParkingSpotStored;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot.ParkingSpotCreated;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ParkingSpotReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ReservationRequestForWholeParkingSpotStored;

public interface ParkingSpotReservationRequestsViews {

    Set<ParkingSpotReservationRequestsView> getAllParkingSpots();

    Set<ParkingSpotReservationRequestsView> getAvailableParkingSpots();

    void handle(ParkingSpotCreated event);

    void handle(ReservationRequestForWholeParkingSpotStored event);

    void handle(ReservationRequestForPartOfParkingSpotStored event);

    void handle(ParkingSpotReservationRequestCancelled event);

}
