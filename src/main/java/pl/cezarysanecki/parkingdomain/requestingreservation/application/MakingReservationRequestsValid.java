package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;

import java.time.Instant;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ReservationRequestEvent.ReservationRequestsConfirmed;

@Slf4j
@RequiredArgsConstructor
public class MakingReservationRequestsValid {

    private final ReservationRequestsTimeSlotRepository reservationRequestsTimeSlotRepository;
    private final ReservationRequesterRepository reservationRequesterRepository;
    private final ReservationRequestEventPublisher reservationRequestEventPublisher;

    public void makeValidAllSince(Instant date) {
        List<ReservationRequestsTimeSlot> reservationRequestsTimeSlots = reservationRequestsTimeSlotRepository.findAllValidSince(sinceDateInstant);
        log.debug("found {} reservation requests to make them valid", reservationRequestsTimeSlots.size());

        List<ReservationRequestEvent> events = reservationRequestsTimeSlots.map(
                reservationRequestsTimeSlot -> {
                    List<ReservationRequest> requestsToRemove = reservationRequestsTimeSlot.getReservationRequests()
                            .keySet()
                            .flatMap(reservationRequestId -> removeReservation(reservationRequestsTimeSlot, reservationRequestId)
                                    .onFailure(t -> log.error("cannot remove reservation request with id {}, reason {}", reservationRequestId, t.getMessage()))
                                    .onSuccess(reservationRequest -> log.debug("removed reservation request with id {}", reservationRequest.reservationRequestId())))
                            .toList();
                    return new ReservationRequestsConfirmed(
                            reservationRequestsTimeSlot.getParkingSpotId(),
                            reservationRequestsTimeSlot.getTimeSlotId(),
                            requestsToRemove);
                });

        reservationRequestEventPublisher.publish(events);
    }

    private Try<ReservationRequest> removeReservation(
            ReservationRequestsTimeSlot reservationRequestsTimeSlot,
            ReservationRequestId reservationRequestId
    ) {
        Option<ReservationRequester> requester = reservationRequesterRepository.findBy(reservationRequestId);
        if (requester.isDefined()) {
            return RemovingReservationRequest.remove(
                    reservationRequestsTimeSlot,
                    requester.get(),
                    reservationRequestId);
        }
        return reservationRequestsTimeSlot.remove(reservationRequestId);
    }

}
