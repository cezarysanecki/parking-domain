package pl.cezarysanecki.parkingdomain.requesting.parkingspot.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancelled;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.ParkingSpotRequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.ParkingSpotRequestCancelled;

@Slf4j
@RequiredArgsConstructor
public class CancellingParkingSpotRequestEventHandler {

    private final ParkingSpotRequestsRepository parkingSpotRequestsRepository;

    @EventListener
    public void handle(RequestCancelled requestCancelled) {
        RequestId requestId = requestCancelled.getRequestId();

        parkingSpotRequestsRepository.findBy(requestId)
                .map(parkingSpotRequests -> {
                    Either<ParkingSpotRequestCancellationFailed, ParkingSpotRequestCancelled> result = parkingSpotRequests.cancel(requestId);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> log.error("cannot find parking spot requests to cancel request with id {}", requestId));
    }

    private Result publishEvents(ParkingSpotRequestCancellationFailed parkingSpotRequestCancellationFailed) {
        log.debug("failed to cancel parking spot request with id {}, reason: {}", parkingSpotRequestCancellationFailed.getParkingSpotId(), parkingSpotRequestCancellationFailed.getReason());
        parkingSpotRequestsRepository.publish(parkingSpotRequestCancellationFailed);
        return Result.Rejection.with(parkingSpotRequestCancellationFailed.getReason());
    }

    private Result publishEvents(ParkingSpotRequestCancelled parkingSpotRequestCancelled) {
        log.debug("successfully cancelled parking spot request with id {}", parkingSpotRequestCancelled.getParkingSpotId());
        parkingSpotRequestsRepository.publish(parkingSpotRequestCancelled);
        return new Result.Success<>(parkingSpotRequestCancelled.getParkingSpotId());
    }

}
