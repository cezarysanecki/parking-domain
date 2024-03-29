package pl.cezarysanecki.parkingdomain.parkingspot.parking.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleEvent.VehicleDroveAway;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleId;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotLeavingOutFailed;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotLeftEvents;

@Slf4j
@RequiredArgsConstructor
public class VehicleDroveAwayEventHandler {

    private final ParkingSpots parkingSpots;

    @EventListener
    public void handle(VehicleDroveAway vehicleDroveAway) {
        VehicleId vehicleId = vehicleDroveAway.getVehicleId();

        parkingSpots.findOccupiedBy(vehicleId)
                .map(occupiedParkingSpot -> {
                    Either<ParkingSpotLeavingOutFailed, ParkingSpotLeftEvents> result = occupiedParkingSpot.release(vehicleId);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents)
                    );
                })
                .onEmpty(() -> log.debug("cannot find occupied parking spot by vehicle with id {}", vehicleId));
    }

    private Result publishEvents(ParkingSpotLeavingOutFailed parkingSpotLeavingOutFailed) {
        log.debug("failed to leave parking spot with id {}, reason: {}", parkingSpotLeavingOutFailed.getParkingSpotId(), parkingSpotLeavingOutFailed.getReason());
        parkingSpots.publish(parkingSpotLeavingOutFailed);
        return Result.Rejection.with(parkingSpotLeavingOutFailed.getReason());
    }

    private Result publishEvents(ParkingSpotLeftEvents parkingSpotLeftEvents) {
        log.debug("successfully left parking spot with id {}", parkingSpotLeftEvents.getParkingSpotId());
        parkingSpots.publish(parkingSpotLeftEvents);
        return new Result.Success();
    }

}
