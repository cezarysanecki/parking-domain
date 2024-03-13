package pl.cezarysanecki.parkingdomain.managment;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

import java.util.UUID;

@Value
public class CreatedNewParkingSpot implements DomainEvent {

    UUID parkingSpotId;

}
