package pl.cezarysanecki.parkingdomain.parking.parkingspot.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleDroveAway;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ReleasingFailed;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ReleasedEvents;

@Slf4j
@RequiredArgsConstructor
public class VehicleDroveAwayEventHandler {

    private final ParkingSpots parkingSpots;

    @EventListener
    public void handle(VehicleDroveAway vehicleDroveAway) {
        VehicleId vehicleId = vehicleDroveAway.vehicleId();

        parkingSpots.findBy(vehicleId)
                .map(parkingSpot -> {
                    Either<ReleasingFailed, ReleasedEvents> result = parkingSpot.release(vehicleId);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> log.debug("cannot find occupied parking spot by vehicle with id {}", vehicleId));
    }

    private Result publishEvents(ReleasingFailed releasingFailed) {
        log.debug("failed to leave parking spot with id {}, reason: {}", releasingFailed.getParkingSpotId(), releasingFailed.getReason());
        parkingSpots.publish(releasingFailed);
        return Result.Rejection.with(releasingFailed.getReason());
    }

    private Result publishEvents(ReleasedEvents releasedEvents) {
        log.debug("successfully left parking spot with id {}", releasedEvents.getParkingSpotId());
        parkingSpots.publish(releasedEvents);
        return new Result.Success<>(releasedEvents.getParkingSpotId());
    }

}
