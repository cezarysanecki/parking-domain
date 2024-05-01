package pl.cezarysanecki.parkingdomain.requesting.parkingspot.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;

@Slf4j
@RequiredArgsConstructor
public class CreatingParkingSpotRequestsEventsHandler {

    private final ParkingSpotRequestsRepository parkingSpotRequestsRepository;

    @EventListener
    public void handle(ParkingSpotAdded parkingSpotAdded) {
        ParkingSpotId parkingSpotId = parkingSpotAdded.parkingSpotId();
        ParkingSpotCapacity parkingSpotCapacity = parkingSpotAdded.capacity();

        log.debug("created parking spot requests for parking spot with id {}", parkingSpotId);
        parkingSpotRequestsRepository.createUsing(parkingSpotId, parkingSpotCapacity);
    }

}
