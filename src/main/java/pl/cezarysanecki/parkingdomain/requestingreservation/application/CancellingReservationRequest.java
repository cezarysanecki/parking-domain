package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.RemovingReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.ReservationRequestEvent.ReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.ReservationRequestEventPublisher;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationRequest {

    private final ReservationRequesterRepository reservationRequesterRepository;
    private final ReservationRequestsTimeSlotsRepository reservationRequestsTimeSlotsRepository;
    private final ReservationRequestEventPublisher reservationRequestEventPublisher;

    public Try<ReservationRequest> cancelRequest(ReservationRequestId reservationRequestId) {
        ReservationRequestsTimeSlot reservationRequestsTimeSlot = findParkingSpotReservationRequestsBy(reservationRequestId);
        ReservationRequester reservationRequester = findReservationRequesterBy(reservationRequestId);

        Try<ReservationRequest> result = RemovingReservationRequest.remove(
                reservationRequestsTimeSlot,
                reservationRequester,
                reservationRequestId);

        return result
                .onFailure(exception -> log.error("cannot cancel reservation request, reason: {}", exception.getMessage()))
                .onSuccess(reservationRequest -> {
                    log.debug("successfully cancelled reservation request with id {}", reservationRequest.getReservationRequestId());
                    reservationRequestEventPublisher.publish(new ReservationRequestCancelled(
                            reservationRequestsTimeSlot.getParkingSpotId(),
                            reservationRequestsTimeSlot.getReservationRequestsTimeSlotId(),
                            reservationRequest));
                });
    }

    private ReservationRequester findReservationRequesterBy(ReservationRequestId reservationRequesterId) {
        return reservationRequesterRepository.findBy(reservationRequesterId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find reservation requester containing request with id: " + reservationRequesterId));
    }

    private ReservationRequestsTimeSlot findParkingSpotReservationRequestsBy(ReservationRequestId reservationRequestId) {
        return reservationRequestsTimeSlotsRepository.findBy(reservationRequestId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot reservation requests containing request with id: " + reservationRequestId));
    }

}
