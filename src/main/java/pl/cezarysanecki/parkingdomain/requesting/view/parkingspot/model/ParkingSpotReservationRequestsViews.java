package pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model;

import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForPartOfParkingSpotStored;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot.ParkingSpotCreated;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.ParkingSpotRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForWholeParkingSpotStored;

public interface ParkingSpotReservationRequestsViews {

    Set<ParkingSpotReservationRequestsView> getAllParkingSpots();

    Set<ParkingSpotReservationRequestsView> getAvailableParkingSpots();

    void handle(ParkingSpotCreated event);

    void handle(RequestForWholeParkingSpotStored event);

    void handle(RequestForPartOfParkingSpotStored event);

    void handle(ParkingSpotRequestCancelled event);

}
