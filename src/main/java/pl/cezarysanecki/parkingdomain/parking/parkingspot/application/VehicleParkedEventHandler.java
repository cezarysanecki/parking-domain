package pl.cezarysanecki.parkingdomain.parking.parkingspot.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleParked;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;

@Slf4j
@RequiredArgsConstructor
public class VehicleParkedEventHandler {

    private final ParkingSpots parkingSpots;

    @EventListener
    public void handle(VehicleParked vehicleParked) {
        VehicleId vehicleId = vehicleParked.getVehicleId();
        VehicleSize vehicleSize = vehicleParked.getVehicleSize();
        ParkingSpotId parkingSpotId = vehicleParked.getParkingSpotId();

        parkingSpots.findOpenBy(parkingSpotId)
                .map(openParkingSpot -> {
                    Either<ParkingSpotEvent.ParkingSpotOccupationFailed, ParkingSpotEvent.ParkingSpotOccupiedEvents> result = openParkingSpot.occupy(vehicleId, vehicleSize);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents)
                    );
                })
                .onEmpty(() -> {
                    log.debug("cannot find parking spot with id {}", parkingSpotId);
                    parkingSpots.publish(new ParkingSpotEvent.ParkingSpotOccupationFailed(parkingSpotId, vehicleId, "cannot find parking spot"));
                });
    }

    private Result publishEvents(ParkingSpotEvent.ParkingSpotOccupationFailed parkingSpotOccupationFailed) {
        log.debug("failed to occupy parking spot with id {}, reason: {}", parkingSpotOccupationFailed.getParkingSpotId(), parkingSpotOccupationFailed.getReason());
        parkingSpots.publish(parkingSpotOccupationFailed);
        return Result.Rejection.with(parkingSpotOccupationFailed.getReason());
    }

    private Result publishEvents(ParkingSpotEvent.ParkingSpotOccupiedEvents parkingSpotOccupiedEvents) {
        log.debug("successfully occupied parking spot with id {}", parkingSpotOccupiedEvents.getParkingSpotId());
        parkingSpots.publish(parkingSpotOccupiedEvents);
        return new Result.Success();
    }

}
