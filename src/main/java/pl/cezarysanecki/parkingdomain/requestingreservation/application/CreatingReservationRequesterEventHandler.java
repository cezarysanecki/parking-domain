package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;

import static pl.cezarysanecki.parkingdomain.management.client.ClientRegistered.BusinessClientRegistered;
import static pl.cezarysanecki.parkingdomain.management.client.ClientRegistered.IndividualClientRegistered;

@Slf4j
@RequiredArgsConstructor
public class CreatingReservationRequesterEventHandler {

    private final ReservationRequesterRepository reservationRequesterRepository;

    @EventListener
    public void handle(IndividualClientRegistered event) {
        ReservationRequesterId requesterId = ReservationRequesterId.of(event.clientId().getValue());

        ReservationRequester requester = ReservationRequester.ofLimit(requesterId, 1);

        log.debug("storing requester with lower limit with id: {}", requester.getRequesterId());
        reservationRequesterRepository.save(requester);
    }


    @EventListener
    public void handle(BusinessClientRegistered event) {
        ReservationRequesterId requesterId = ReservationRequesterId.of(event.clientId().getValue());

        ReservationRequester requester = ReservationRequester.ofLimit(requesterId, 20);

        log.debug("storing requester with higher limit with id: {}", requester.getRequesterId());
        reservationRequesterRepository.save(requester);
    }

}
