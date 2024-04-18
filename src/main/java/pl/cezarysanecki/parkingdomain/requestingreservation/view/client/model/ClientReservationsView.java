package pl.cezarysanecki.parkingdomain.requestingreservation.view.client.model;

import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class ClientReservationsView {

    UUID clientId;
    Set<UUID> currentReservations;

}
