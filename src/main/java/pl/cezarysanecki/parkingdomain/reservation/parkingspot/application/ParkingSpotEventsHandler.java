package pl.cezarysanecki.parkingdomain.reservation.parkingspot.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationsRepository;

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot.ParkingSpotCreated;

@RequiredArgsConstructor
public class ParkingSpotEventsHandler {

    private final ParkingSpotReservationsRepository parkingSpotReservationsRepository;

    @EventListener
    public void handle(ParkingSpotCreated parkingSpotCreated) {
        ParkingSpotId parkingSpotId = parkingSpotCreated.getParkingSpotId();
        ParkingSpotCapacity parkingSpotCapacity = parkingSpotCreated.getParkingSpotCapacity();

        parkingSpotReservationsRepository.createUsing(parkingSpotId, parkingSpotCapacity);
    }

}
