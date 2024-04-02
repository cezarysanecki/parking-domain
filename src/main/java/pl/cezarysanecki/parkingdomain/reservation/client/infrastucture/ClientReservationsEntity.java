package pl.cezarysanecki.parkingdomain.reservation.client.infrastucture;

import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent;

import java.util.Set;
import java.util.UUID;

class ClientReservationsEntity {

    UUID clientId;
    Set<UUID> reservations;

    void handle(ClientReservationsEvent clientReservationsEvent) {

    }

}
