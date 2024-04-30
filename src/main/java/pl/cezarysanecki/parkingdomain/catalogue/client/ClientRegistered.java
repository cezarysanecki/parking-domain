package pl.cezarysanecki.parkingdomain.catalogue.client;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

public record ClientRegistered(
        ClientId clientId,
        PhoneNumber phoneNumber
) implements DomainEvent {

    ClientRegistered(Client client) {
        this(client.getClientId(), client.getPhoneNumber());
    }

}
