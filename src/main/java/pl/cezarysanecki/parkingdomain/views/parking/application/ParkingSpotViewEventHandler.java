package pl.cezarysanecki.parkingdomain.views.parking.application;

import io.vavr.API;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.views.parking.model.ParkingViews;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked;

@Slf4j
@RequiredArgsConstructor
public class ParkingSpotViewEventHandler {

    private final ParkingViews parkingViews;

    @EventListener
    public void handle(ParkingSpotEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ParkingSpotCreated.class)), this::handle),
                Case($(instanceOf(VehicleLeft.class)), this::handle),
                Case($(instanceOf(VehicleParked.class)), this::handle),
                Case($(instanceOf(FullyOccupied.class)), this::handle),
                Case($(), () -> event));
    }

    private ParkingSpotEvent handle(ParkingSpotCreated parkingSpotCreated) {
        parkingViews.addParkingSpot(parkingSpotCreated.getParkingSpotId(), parkingSpotCreated.getParkingSpotType(), parkingSpotCreated.getCapacity());
        return parkingSpotCreated;
    }

    private ParkingSpotEvent handle(VehicleLeft vehicleLeft) {
        parkingViews.increaseCapacity(vehicleLeft.getParkingSpotId(), vehicleLeft.getVehicle().getVehicleSizeUnit().getValue());
        return vehicleLeft;
    }

    private ParkingSpotEvent handle(VehicleParked vehicleParked) {
        parkingViews.decreaseCapacity(vehicleParked.getParkingSpotId(), vehicleParked.getVehicle().getVehicleSizeUnit().getValue());
        return vehicleParked;
    }

    private ParkingSpotEvent handle(FullyOccupied fullyOccupied) {
        parkingViews.removeParkingSpot(fullyOccupied.getParkingSpotId());
        return fullyOccupied;
    }

}
