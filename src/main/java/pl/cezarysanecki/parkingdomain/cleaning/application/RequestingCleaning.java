package pl.cezarysanecki.parkingdomain.cleaning.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.application.ProvidingUsageOfParkingSpot;

import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvents.ParkingSpotReleased;

@Slf4j
@RequiredArgsConstructor
public class RequestingCleaning {

    private final ProvidingUsageOfParkingSpot providingUsageOfParkingSpot;

    @EventListener
    public void handle(ParkingSpotReleased event) {
        log.debug("handling parking spot with id {} released to request cleaning", event.parkingSpotId());
        Try<Result> result = providingUsageOfParkingSpot.makeOutOfUse(event.parkingSpotId());
    }

}
