package pl.cezarysanecki.parkingdomain.requesting.parkingspot.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent;
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForWholeParkingSpotStored;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.*;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForPartOfParkingSpotStored;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.StoringParkingSpotRequestFailed;

@Slf4j
@RequiredArgsConstructor
public class StoringParkingSpotRequestEventHandler {

    private final ParkingSpotRequestsRepository parkingSpotRequestsRepository;

    @EventListener
    public void handle(RequestForPartOfParkingSpotMade requestForPartOfParkingSpotMade) {
        ParkingSpotId parkingSpotId = requestForPartOfParkingSpotMade.getParkingSpotId();
        RequestId requestId = requestForPartOfParkingSpotMade.getRequestId();
        VehicleSize vehicleSize = requestForPartOfParkingSpotMade.getVehicleSize();

        parkingSpotRequestsRepository.findBy(parkingSpotId)
                .map(parkingSpotRequests -> {
                    Either<StoringParkingSpotRequestFailed, RequestForPartOfParkingSpotStored> result = parkingSpotRequests.storeRequest(requestId, vehicleSize);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> {
                    log.error("cannot find parking spot requests");
                    parkingSpotRequestsRepository.publish(new StoringParkingSpotRequestFailed(parkingSpotId, requestId, "cannot find parking spot requests"));
                });
    }

    @EventListener
    public void handle(RequestForWholeParkingSpotMade requestForWholeParkingSpotMade) {
        ParkingSpotId parkingSpotId = requestForWholeParkingSpotMade.getParkingSpotId();
        RequestId requestId = requestForWholeParkingSpotMade.getRequestId();

        parkingSpotRequestsRepository.findBy(parkingSpotId)
                .map(parkingSpotRequests -> {
                    Either<StoringParkingSpotRequestFailed, RequestForWholeParkingSpotStored> result = parkingSpotRequests.storeRequest(requestId);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> {
                    log.error("cannot find parking spot requests");
                    parkingSpotRequestsRepository.publish(new StoringParkingSpotRequestFailed(parkingSpotId, requestId, "cannot find parking spot requests"));
                });
    }

    private Result publishEvents(StoringParkingSpotRequestFailed storingParkingSpotRequestFailed) {
        log.debug("failed to request parking spot with id {}, reason: {}", storingParkingSpotRequestFailed.getParkingSpotId(), storingParkingSpotRequestFailed.getReason());
        parkingSpotRequestsRepository.publish(storingParkingSpotRequestFailed);
        return Result.Rejection.with(storingParkingSpotRequestFailed.getReason());
    }

    private Result publishEvents(RequestForPartOfParkingSpotStored requestForPartOfParkingSpotStored) {
        log.debug("successfully stored request for part of parking spot with id {}", requestForPartOfParkingSpotStored.getParkingSpotId());
        parkingSpotRequestsRepository.publish(requestForPartOfParkingSpotStored);
        return new Result.Success<>(requestForPartOfParkingSpotStored.getParkingSpotId());
    }

    private Result publishEvents(RequestForWholeParkingSpotStored requestForWholeParkingSpotStored) {
        log.debug("successfully stored request for whole parking spot with id {}", requestForWholeParkingSpotStored.getParkingSpotId());
        parkingSpotRequestsRepository.publish(requestForWholeParkingSpotStored);
        return new Result.Success<>(requestForWholeParkingSpotStored.getParkingSpotId());
    }

}
