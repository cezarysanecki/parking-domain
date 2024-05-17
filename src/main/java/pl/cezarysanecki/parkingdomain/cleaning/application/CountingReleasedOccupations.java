package pl.cezarysanecki.parkingdomain.cleaning.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.cleaning.model.CleaningEvents;
import pl.cezarysanecki.parkingdomain.cleaning.model.CleaningRepository;
import pl.cezarysanecki.parkingdomain.cleaning.model.CurrentCounterValue;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvents;

import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvents.*;

@RequiredArgsConstructor
public class CountingReleasedOccupations {

    private final CleaningRepository cleaningRepository;

    @EventListener
    public void handle(ParkingSpotReleased event) {
        CurrentCounterValue currentCounterValue = cleaningRepository.increaseCounterFor(event.parkingSpotId());
    }

}
