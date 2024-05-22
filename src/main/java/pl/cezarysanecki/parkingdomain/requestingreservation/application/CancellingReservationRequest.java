package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationRequest {

    private final ReservationRequesterRepository reservationRequesterRepository;
    private final ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository;

    public Try<ReservationRequest> cancelRequest(ReservationRequestId reservationRequestId) {
        ReservationRequester reservationRequester = findReservationRequesterBy(reservationRequestId);
        ParkingSpotReservationRequests parkingSpotReservationRequests = findParkingSpotReservationRequestsBy(reservationRequestId);

        Try<ReservationRequestCancelled> cancellationResult = parkingSpotReservationRequests.cancel(reservationRequestId);
        if (cancellationResult.isFailure()) {
            log.error("cannot cancel reservation request, reason: {}", cancellationResult.getCause().getMessage());
            return cancellationResult.map(ReservationRequestCancelled::reservationRequest);
        }
        ReservationRequestCancelled event = cancellationResult.get();

        return reservationRequester.remove(event.reservationRequest().getReservationRequestId())
                .onFailure(exception -> log.error("cannot cancel reservation request, reason: {}", exception.getMessage()))
                .onSuccess(removedReservationRequestId -> {
                    reservationRequesterRepository.save(reservationRequester);
                    parkingSpotReservationRequestsRepository.publish(event);
                })
                .map(removedReservationRequestId -> event.reservationRequest());
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
