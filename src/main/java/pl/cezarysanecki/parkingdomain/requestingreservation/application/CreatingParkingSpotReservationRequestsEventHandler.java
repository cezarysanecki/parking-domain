package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;

@Slf4j
@RequiredArgsConstructor
public class CreatingParkingSpotReservationRequestsEventHandler {

    private final ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository;

    @EventListener
    public void handle(ParkingSpotAdded event) {
        ParkingSpotReservationRequests parkingSpotReservationRequests = ParkingSpotReservationRequests.newOne(
                event.parkingSpotId(), event.capacity());
        log.debug("storing parking spot as reservation requests with id: {}", parkingSpotReservationRequests.getParkingSpotId());
        parkingSpotReservationRequestsRepository.save(parkingSpotReservationRequests);
    }

}
