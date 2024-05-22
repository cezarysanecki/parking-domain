package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.AppendingReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.ReservationRequestEventPublisher;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotsRepository;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ReservationRequestEvent.ReservationRequestStored;

@Slf4j
@RequiredArgsConstructor
public class StoringReservationRequest {

    private final ReservationRequesterRepository reservationRequesterRepository;
    private final ReservationRequestsTimeSlotsRepository reservationRequestsTimeSlotsRepository;
    private final ReservationRequestEventPublisher reservationRequestEventPublisher;

    public Try<ReservationRequest> storeRequest(
            ReservationRequesterId requesterId,
            ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
            SpotUnits spotUnits
    ) {
        ReservationRequestsTimeSlot reservationRequestsTimeSlot = findParkingSpotReservationRequestsBy(reservationRequestsTimeSlotId);
        ReservationRequester reservationRequester = findReservationRequesterBy(requesterId);

        Try<ReservationRequest> result = AppendingReservationRequest.append(
                reservationRequestsTimeSlot,
                reservationRequester,
                spotUnits);

        return result
                .onFailure(exception -> log.error("cannot store reservation request, reason: {}", exception.getMessage()))
                .onSuccess(reservationRequest -> {
                    log.debug("successfully stored reservation request with id {}", reservationRequest.getReservationRequestId());
                    reservationRequestEventPublisher.publish(new ReservationRequestStored(
                            reservationRequestsTimeSlot.getParkingSpotId(),
                            reservationRequestsTimeSlot.getReservationRequestsTimeSlotId(),
                            reservationRequest));
                });
    }

    private ReservationRequestsTimeSlot findParkingSpotReservationRequestsBy(ReservationRequestsTimeSlotId reservationRequestsTimeSlotId) {
        return reservationRequestsTimeSlotsRepository.findBy(reservationRequestsTimeSlotId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot reservation requests with id: " + reservationRequestsTimeSlotId));
    }

    private ReservationRequester findReservationRequesterBy(ReservationRequesterId requesterId) {
        return reservationRequesterRepository.findBy(requesterId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find reservation requester with id: " + requesterId));
    }

}
