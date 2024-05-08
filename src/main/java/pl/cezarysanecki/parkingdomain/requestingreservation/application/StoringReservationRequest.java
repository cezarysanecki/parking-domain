package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestStored;

@Slf4j
@RequiredArgsConstructor
public class StoringReservationRequest {

    private final EventPublisher eventPublisher;
    private final ReservationRequesterRepository reservationRequesterRepository;
    private final ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository;

    public Try<ReservationRequest> storeRequest(
            ReservationRequesterId requesterId,
            ParkingSpotId parkingSpotId,
            SpotUnits spotUnits
    ) {
        ReservationRequester reservationRequester = findReservationRequesterBy(requesterId);
        ParkingSpotReservationRequests parkingSpotReservationRequests = findParkingSpotReservationRequestsBy(parkingSpotId);

        return parkingSpotReservationRequests.storeRequest(requesterId, spotUnits)
                .flatMap(reservationRequester::append)
                .onFailure(exception -> log.error("cannot store reservation request, reason: {}", exception.getMessage()))
                .onSuccess(reservationRequest -> {
                    reservationRequesterRepository.save(reservationRequester);
                    parkingSpotReservationRequestsRepository.save(parkingSpotReservationRequests);

                    eventPublisher.publish(new ReservationRequestStored(parkingSpotReservationRequests.getParkingSpotId(), reservationRequest));
                });
    }

    private ReservationRequester findReservationRequesterBy(ReservationRequesterId requesterId) {
        return reservationRequesterRepository.findBy(requesterId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find reservation requester with id: " + requesterId));
    }

    private ParkingSpotReservationRequests findParkingSpotReservationRequestsBy(ParkingSpotId parkingSpotId) {
        return parkingSpotReservationRequestsRepository.findBy(parkingSpotId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot reservation requests with id: " + parkingSpotId));
    }

}
