package pl.cezarysanecki.parkingdomain.requesting.client.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId;
import pl.cezarysanecki.parkingdomain.requesting.client.application.CancellingRequest.Command;
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId;

import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.StoringParkingSpotRequestFailed;

@Slf4j
@RequiredArgsConstructor
public class CancellingRequestEventHandler {

    private final CancellingRequest cancellingRequest;

    @EventListener
    public void handle(StoringParkingSpotRequestFailed storingParkingSpotReservationRequestFailed) {
        ReservationId reservationId = storingParkingSpotReservationRequestFailed.getReservationId();

        cancellingRequest.cancelRequest(new Command(RequestId.of(reservationId.getValue())))
                .onFailure(exception -> log.error(exception.getMessage(), exception));
    }

}
