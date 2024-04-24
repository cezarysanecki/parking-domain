package pl.cezarysanecki.parkingdomain.requesting.view.client.model;

import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class ClientReservationRequestsView {

    UUID clientId;
    Set<UUID> currentReservationRequests;

}
