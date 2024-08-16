package pl.cezarysanecki.parkingdomain.management.client.api;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

public record IndividualClientRegistered(
    ClientId clientId
) implements DomainEvent {
}
