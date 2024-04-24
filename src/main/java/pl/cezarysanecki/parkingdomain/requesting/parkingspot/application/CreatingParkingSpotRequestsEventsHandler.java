package pl.cezarysanecki.parkingdomain.requesting.parkingspot.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository;

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot.ParkingSpotCreated;

@Slf4j
@RequiredArgsConstructor
public class CreatingParkingSpotRequestsEventsHandler {

    private final ParkingSpotRequestsRepository parkingSpotRequestsRepository;

    @EventListener
    public void handle(ParkingSpotCreated parkingSpotCreated) {
        ParkingSpotId parkingSpotId = parkingSpotCreated.getParkingSpotId();
        ParkingSpotCapacity parkingSpotCapacity = parkingSpotCreated.getParkingSpotCapacity();

        log.debug("created parking spot requests for parking spot with id {}", parkingSpotId);
        parkingSpotRequestsRepository.createUsing(parkingSpotId, parkingSpotCapacity);
    }

}
