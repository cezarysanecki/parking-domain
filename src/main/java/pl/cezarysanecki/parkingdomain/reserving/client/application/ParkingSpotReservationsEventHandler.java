package pl.cezarysanecki.parkingdomain.reserving.client.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId;

import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationFailed;

@Slf4j
@RequiredArgsConstructor
public class ParkingSpotReservationsEventHandler {

    private final CancellingReservationRequest cancellingReservationRequest;

    @EventListener
    public void handle(ParkingSpotReservationFailed parkingSpotReservationFailed) {
        ReservationId reservationId = parkingSpotReservationFailed.getReservationId();

        cancellingReservationRequest.cancelReservationRequest(new CancellingReservationRequest.Command(reservationId));
    }

}
