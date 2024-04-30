package pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model;

import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForPartOfParkingSpotStored;

import java.util.Set;

import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotAdded;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.ParkingSpotRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForWholeParkingSpotStored;

public interface ParkingSpotRequestsViews {

    Set<ParkingSpotRequestsView> getAllParkingSpots();

    Set<ParkingSpotRequestsView> getAvailableParkingSpots();

    void handle(ParkingSpotAdded event);

    void handle(RequestForWholeParkingSpotStored event);

    void handle(RequestForPartOfParkingSpotStored event);

    void handle(ParkingSpotRequestCancelled event);

}
