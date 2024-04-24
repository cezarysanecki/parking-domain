package pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model;

import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class ParkingSpotReservationRequestsView {

    UUID parkingSpotId;
    int spaceLeft;
    Set<UUID> currentReservationRequests;

}
