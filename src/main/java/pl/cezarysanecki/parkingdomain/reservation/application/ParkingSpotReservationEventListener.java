package pl.cezarysanecki.parkingdomain.reservation.application;

import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsRepository;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@RequiredArgsConstructor
public class ParkingSpotReservationEventListener {

    private final ParkingSpotReservationsRepository parkingSpotReservationsRepository;

    @EventListener
    public void handle(ParkingSpotEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ParkingSpotCreated.class)), this::handleEvent),
                Case($(), () -> event));
    }

    private ParkingSpotEvent handleEvent(ParkingSpotCreated parkingSpotCreated) {
        ParkingSpotId parkingSpotId = parkingSpotCreated.getParkingSpotId();
        ParkingSpotType parkingSpotType = parkingSpotCreated.getParkingSpotType();
        int capacity = parkingSpotCreated.getCapacity();

        parkingSpotReservationsRepository.createFor(parkingSpotId, parkingSpotType, capacity);
        return parkingSpotCreated;
    }

}
