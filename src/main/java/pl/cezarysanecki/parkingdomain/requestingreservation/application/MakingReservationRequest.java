package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestsEvent.ReservationRequestMade;

@Slf4j
@RequiredArgsConstructor
public class MakingReservationRequest {

    private final ReservationRequestsRepository reservationRequestsRepository;

    public Try<ReservationRequest> makeRequest(
            ReservationRequesterId requesterId,
            ReservationRequestsTimeSlotId timeSlotId,
            SpotUnits spotUnits
    ) {
        ReservationRequests reservationRequests = findBy(requesterId, timeSlotId);

        Try<ReservationRequestMade> result = reservationRequests.makeRequest(spotUnits);

        return result
                .onFailure(exception -> log.error("cannot make reservation request, reason: {}", exception.getMessage()))
                .onSuccess(event -> {
                    log.debug("successfully made reservation request with id {}", event.reservationRequest().reservationRequestId());
                    reservationRequestsRepository.publish(event);
                })
                .map(ReservationRequestMade::reservationRequest);
    }

    private ReservationRequests findBy(ReservationRequesterId requesterId, ReservationRequestsTimeSlotId timeSlotId) {
        return reservationRequestsRepository.findBy(requesterId, timeSlotId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find reservation requests by requester with id: " + requesterId + " and time slot with id: " + timeSlotId));
    }

}
