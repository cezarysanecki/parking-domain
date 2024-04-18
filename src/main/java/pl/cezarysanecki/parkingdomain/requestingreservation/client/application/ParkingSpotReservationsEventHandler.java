package pl.cezarysanecki.parkingdomain.requestingreservation.client.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;

import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ParkingSpotReservationRequestFailed;

@Slf4j
@RequiredArgsConstructor
public class ParkingSpotReservationsEventHandler {

    private final CancellingReservationRequest cancellingReservationRequest;

    @EventListener
    public void handle(ParkingSpotReservationRequestFailed parkingSpotReservationFailed) {
        ReservationId reservationId = parkingSpotReservationFailed.getReservationId();

        cancellingReservationRequest.cancelReservationRequest(new CancellingReservationRequest.Command(reservationId))
                .onFailure(exception -> log.error(exception.getMessage(), exception));
    }

}
