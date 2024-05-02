package pl.cezarysanecki.parkingdomain.parking.parkingspot.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotUnits;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleParked;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.OccupationFailed;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.OccupiedEvents;

@Slf4j
@RequiredArgsConstructor
public class VehicleParkedEventHandler {

    private final ParkingSpots parkingSpots;

    @EventListener
    public void handle(VehicleParked vehicleParked) {
        VehicleId vehicleId = vehicleParked.vehicleId();
        SpotUnits spotUnits = vehicleParked.getSpotUnits();
        ParkingSpotId parkingSpotId = vehicleParked.getParkingSpotId();

        parkingSpots.findBy(parkingSpotId)
                .map(openParkingSpot -> {
                    Either<OccupationFailed, OccupiedEvents> result = openParkingSpot.occupy(vehicleId, spotUnits);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> {
                    log.debug("cannot find parking spot with id {}", parkingSpotId);
                    parkingSpots.publish(new OccupationFailed(parkingSpotId, vehicleId, "cannot find parking spot"));
                });
    }

    private Result publishEvents(OccupationFailed occupationFailed) {
        log.debug("failed to occupy parking spot with id {}, reason: {}", occupationFailed.getParkingSpotId(), occupationFailed.getReason());
        parkingSpots.publish(occupationFailed);
        return Result.Rejection.with(occupationFailed.getReason());
    }

    private Result publishEvents(OccupiedEvents occupiedEvents) {
        log.debug("successfully occupied parking spot with id {}", occupiedEvents.getParkingSpotId());
        parkingSpots.publish(occupiedEvents);
        return new Result.Success<>(occupiedEvents.getParkingSpotId());
    }

}
