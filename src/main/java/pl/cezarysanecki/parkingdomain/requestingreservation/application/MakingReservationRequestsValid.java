package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.RemovingReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequest;
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotsRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ReservationRequestEvent.ReservationRequestsConfirmed;

@Slf4j
@RequiredArgsConstructor
public class MakingReservationRequestsValid {

    private final DateProvider dateProvider;
    private final ReservationRequestsTimeSlotsRepository reservationRequestsTimeSlotsRepository;
    private final ReservationRequesterRepository reservationRequesterRepository;
    private final ReservationRequestEventPublisher reservationRequestEventPublisher;
    private final int hoursToMakeReservationRequestValid;

    public void makeValid() {
        LocalDateTime sinceDate = dateProvider.now().plusHours(hoursToMakeReservationRequestValid);
        Instant sinceDateInstant = sinceDate.toInstant(ZoneOffset.UTC);

        List<ReservationRequestsTimeSlot> reservationRequestsTimeSlots = reservationRequestsTimeSlotsRepository.findAllValidSince(sinceDateInstant);
        log.debug("found {} reservation requests to make them valid", reservationRequestsTimeSlots.size());

        List<ReservationRequestEvent> events = reservationRequestsTimeSlots.map(
                reservationRequestsTimeSlot -> {
                    List<ReservationRequest> requestsToRemove = reservationRequestsTimeSlot.getReservationRequests()
                            .keySet()
                            .flatMap(reservationRequestId -> removeReservation(reservationRequestsTimeSlot, reservationRequestId)
                                    .onFailure(t -> log.error("cannot remove reservation request with id {}, reason {}", reservationRequestId, t.getMessage()))
                                    .onSuccess(reservationRequest -> log.debug("removed reservation request with id {}", reservationRequest.getReservationRequestId())))
                            .toList();
                    return new ReservationRequestsConfirmed(
                            reservationRequestsTimeSlot.getParkingSpotId(),
                            reservationRequestsTimeSlot.getReservationRequestsTimeSlotId(),
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
