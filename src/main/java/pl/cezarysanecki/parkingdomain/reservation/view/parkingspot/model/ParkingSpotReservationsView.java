package pl.cezarysanecki.parkingdomain.reservation.view.parkingspot.model;

import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class ParkingSpotReservationsView {

    UUID parkingSpotId;
    int spaceLeft;
    Set<UUID> currentReservations;

}
