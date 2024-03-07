package pl.cezarysanecki.parkingdomain.commons.events;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID getEventId();

    Instant getWhen();

}
