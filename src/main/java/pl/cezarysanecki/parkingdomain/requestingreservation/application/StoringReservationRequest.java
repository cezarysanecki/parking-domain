package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.AppendingReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotsRepository;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequest;

@Slf4j
@RequiredArgsConstructor
public class StoringReservationRequest {

    private final ReservationRequestsTimeSlotsRepository reservationRequestsTimeSlotsRepository;
    private final ReservationRequesterRepository reservationRequesterRepository;

    public Try<ReservationRequest> storeRequest(
            ReservationRequesterId requesterId,
            ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
            SpotUnits spotUnits
    ) {
        ReservationRequestsTimeSlot reservationRequestsTimeSlot = findParkingSpotReservationRequestsBy(reservationRequestsTimeSlotId);
        ReservationRequester reservationRequester = findReservationRequesterBy(requesterId);

        ReservationRequest reservationRequest = ReservationRequest.newOne(requesterId, reservationRequestsTimeSlotId, spotUnits);
        Try<AppendingReservationRequest.Result> result = AppendingReservationRequest.append(
                reservationRequestsTimeSlot,
                reservationRequester,
                reservationRequest);

        return result
                .onFailure(exception -> log.error("cannot store reservation request, reason: {}", exception.getMessage()))
                .onSuccess(appendingResult -> {
                    log.debug("successfully stored reservation request with id {}", reservationRequest.getReservationRequestId());
                    reservationRequestsTimeSlotsRepository.publish(appendingResult.timeSlotEvent());
                    reservationRequesterRepository.publish(appendingResult.requesterEvent());
                })
                .map(appendingResult -> reservationRequest);
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
