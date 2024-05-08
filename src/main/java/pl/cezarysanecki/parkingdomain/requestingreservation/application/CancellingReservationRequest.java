package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationRequest {

    private final EventPublisher eventPublisher;
    private final ReservationRequesterRepository reservationRequesterRepository;
    private final ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository;

    public Try<ReservationRequest> cancelRequest(ReservationRequestId reservationRequestId) {
        ReservationRequester reservationRequester = findReservationRequesterBy(reservationRequestId);
        ParkingSpotReservationRequests parkingSpotReservationRequests = findParkingSpotReservationRequestsBy(reservationRequestId);

        return parkingSpotReservationRequests.cancel(reservationRequestId)
                .flatMap(reservationRequester::remove)
                .onFailure(exception -> log.error("cannot cancel reservation request, reason: {}", exception.getMessage()))
                .onSuccess(reservationRequest -> {
                    reservationRequesterRepository.save(reservationRequester);
                    parkingSpotReservationRequestsRepository.save(parkingSpotReservationRequests);

                    eventPublisher.publish(new ReservationRequestCancelled(parkingSpotReservationRequests.getParkingSpotId(), reservationRequest));
                });
    }

    private ReservationRequester findReservationRequesterBy(ReservationRequestId reservationRequesterId) {
        return reservationRequesterRepository.findBy(reservationRequesterId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find reservation requester containing request with id: " + reservationRequesterId));
    }

    private ParkingSpotReservationRequests findParkingSpotReservationRequestsBy(ReservationRequestId reservationRequestId) {
        return parkingSpotReservationRequestsRepository.findBy(reservationRequestId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot reservation requests containing request with id: " + reservationRequestId));
    }

}
