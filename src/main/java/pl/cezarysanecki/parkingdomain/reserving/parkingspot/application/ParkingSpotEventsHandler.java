package pl.cezarysanecki.parkingdomain.reserving.parkingspot.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationsRepository;

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot.ParkingSpotCreated;

@Slf4j
@RequiredArgsConstructor
public class ParkingSpotEventsHandler {

    private final ParkingSpotReservationsRepository parkingSpotReservationsRepository;

    @EventListener
    public void handle(ParkingSpotCreated parkingSpotCreated) {
        ParkingSpotId parkingSpotId = parkingSpotCreated.getParkingSpotId();
        ParkingSpotCapacity parkingSpotCapacity = parkingSpotCreated.getParkingSpotCapacity();

        log.debug("created parking spot reservations for parking spot with id {}", parkingSpotId);
        parkingSpotReservationsRepository.createUsing(parkingSpotId, parkingSpotCapacity);
    }

}
