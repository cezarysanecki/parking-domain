package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;

@Slf4j
@RequiredArgsConstructor
public class CreatingParkingSpotEventHandler {

    private final ParkingSpotRepository parkingSpotRepository;

    @EventListener
    public void handle(ParkingSpotAdded event) {
        ParkingSpot parkingSpot = ParkingSpot.newOne(event.parkingSpotId(), event.capacity());
        log.debug("storing parking spot as placement with id: {}", parkingSpot.getParkingSpotId());
        parkingSpotRepository.save(parkingSpot);
    }

}
