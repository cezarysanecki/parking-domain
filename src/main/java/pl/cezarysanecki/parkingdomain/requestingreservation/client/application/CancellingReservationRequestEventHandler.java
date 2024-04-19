package pl.cezarysanecki.parkingdomain.requestingreservation.client.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.CancellingReservationRequest.Command;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;

import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.RequestingParkingSpotReservationFailed;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationRequestEventHandler {

    private final CancellingReservationRequest cancellingReservationRequest;

    @EventListener
    public void handle(RequestingParkingSpotReservationFailed requestingParkingSpotReservationFailed) {
        ReservationId reservationId = requestingParkingSpotReservationFailed.getReservationId();

        cancellingReservationRequest.cancelReservationRequest(new Command(reservationId))
                .onFailure(exception -> log.error(exception.getMessage(), exception));
    }

}
