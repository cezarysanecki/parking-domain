package pl.cezarysanecki.parkingdomain.reservationschedule.application;

import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.CompletelyFreedUp;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedules;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@RequiredArgsConstructor
public class ParkingSpotReservationEventListener {

    private final ReservationSchedules reservationSchedules;

    @EventListener
    public void handle(ParkingSpotEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ParkingSpotCreated.class)), this::handleEvent),
                Case($(instanceOf(VehicleParked.class)), this::handleEvent),
                Case($(instanceOf(CompletelyFreedUp.class)), this::handleEvent),
                Case($(), () -> event));
    }

    private ParkingSpotEvent handleEvent(ParkingSpotCreated parkingSpotCreated) {
        reservationSchedules.createFor(parkingSpotCreated.getParkingSpotId());
        return parkingSpotCreated;
    }

    private ParkingSpotEvent handleEvent(VehicleParked vehicleParked) {
        reservationSchedules.markOccupation(vehicleParked.getParkingSpotId(), true);
        return vehicleParked;
    }

    private ParkingSpotEvent handleEvent(CompletelyFreedUp completelyFreedUp) {
        reservationSchedules.markOccupation(completelyFreedUp.getParkingSpotId(), false);
        return completelyFreedUp;
    }

}
