package pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model;

import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class ParkingSpotRequestsView {

    UUID parkingSpotId;
    int spaceLeft;
    Set<UUID> currentRequests;

}
