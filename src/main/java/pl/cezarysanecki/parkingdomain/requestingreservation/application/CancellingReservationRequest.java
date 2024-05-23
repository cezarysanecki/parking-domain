package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.RemovingReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotsRepository;
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequestId;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationRequest {

    private final ReservationRequestsTimeSlotsRepository reservationRequestsTimeSlotsRepository;
    private final ReservationRequesterRepository reservationRequesterRepository;

    public Try<ReservationRequestId> cancelRequest(ReservationRequestId reservationRequestId) {
        ReservationRequestsTimeSlot reservationRequestsTimeSlot = findParkingSpotReservationRequestsBy(reservationRequestId);
        ReservationRequester reservationRequester = findReservationRequesterBy(reservationRequestId);

        Try<RemovingReservationRequest.Result> result = RemovingReservationRequest.remove(
                reservationRequestsTimeSlot,
                reservationRequester,
                reservationRequestId);

        return result
                .onFailure(exception -> log.error("cannot cancel reservation request, reason: {}", exception.getMessage()))
                .onSuccess(removingResult -> {
                    log.debug("successfully cancelled reservation request with id {}", reservationRequestId);
                    reservationRequestsTimeSlotsRepository.publish(removingResult.timeSlotEvent());
                    reservationRequesterRepository.publish(removingResult.requesterEvent());
                })
                .map(removingResult -> reservationRequestId);
    }

    private ReservationRequestsTimeSlot findParkingSpotReservationRequestsBy(ReservationRequestId reservationRequestId) {
        return reservationRequestsTimeSlotsRepository.findBy(reservationRequestId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot reservation requests containing request with id: " + reservationRequestId));
    }

    private ReservationRequester findReservationRequesterBy(ReservationRequestId reservationRequesterId) {
        return reservationRequesterRepository.findBy(reservationRequesterId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find reservation requester containing request with id: " + reservationRequesterId));
    }

}
