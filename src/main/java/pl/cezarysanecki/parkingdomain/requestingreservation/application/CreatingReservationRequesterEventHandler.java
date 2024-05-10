package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.collection.HashSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.client.ClientRegistered;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;

@Slf4j
@RequiredArgsConstructor
public class CreatingReservationRequesterEventHandler {

    private final ReservationRequesterRepository reservationRequesterRepository;

    @EventListener
    public void handle(ClientRegistered event) {
        ReservationRequester requester = new ReservationRequester(
                ReservationRequesterId.of(event.clientId().getValue()),
                HashSet.empty());
        log.debug("storing requester with id: {}", requester.getRequesterId());
        reservationRequesterRepository.save(requester);
    }

}
