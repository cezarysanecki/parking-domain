package pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.model;

import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationEvent.PartOfParkingSpotReserved;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot.ParkingSpotCreated;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationEvent.WholeParkingSpotReserved;

public interface ParkingSpotReservationsViews {

    Set<ParkingSpotReservationsView> getAllParkingSpots();

    Set<ParkingSpotReservationsView> getAvailableParkingSpots();

    void handle(ParkingSpotCreated event);

    void handle(WholeParkingSpotReserved event);

    void handle(PartOfParkingSpotReserved event);

    void handle(ParkingSpotReservationCancelled event);

}
