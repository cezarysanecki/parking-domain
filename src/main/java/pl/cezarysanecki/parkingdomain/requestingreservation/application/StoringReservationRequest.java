package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestStored;

@Slf4j
@RequiredArgsConstructor
public class StoringReservationRequest {

    private final ReservationRequesterRepository reservationRequesterRepository;
    private final ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository;

    public Try<ReservationRequest> storeRequest(
            ReservationRequesterId requesterId,
            ParkingSpotCategory parkingSpotCategory,
            TimeSlot timeSlot,
            SpotUnits spotUnits
    ) {
        ParkingSpotReservationRequests parkingSpotReservationRequests = findParkingSpotReservationRequestsBy(parkingSpotCategory, timeSlot);

        return handleRequest(parkingSpotReservationRequests, requesterId, spotUnits);
    }

    public Try<ReservationRequest> storeRequest(
            ReservationRequesterId requesterId,
            ParkingSpotTimeSlotId parkingSpotTimeSlotId,
            SpotUnits spotUnits
    ) {
        ParkingSpotReservationRequests parkingSpotReservationRequests = findParkingSpotReservationRequestsBy(parkingSpotTimeSlotId);

        return handleRequest(parkingSpotReservationRequests, requesterId, spotUnits);
    }

    private Try<ReservationRequest> handleRequest(
            ParkingSpotReservationRequests parkingSpotReservationRequests,
            ReservationRequesterId requesterId,
            SpotUnits spotUnits
    ) {
        ReservationRequester reservationRequester = findReservationRequesterBy(requesterId);

        Try<ReservationRequestStored> storageResult = parkingSpotReservationRequests.storeRequest(requesterId, spotUnits);
        if (storageResult.isFailure()) {
            log.error("cannot store reservation request, reason: {}", storageResult.getCause().getMessage());
            return storageResult.map(ReservationRequestStored::reservationRequest);
        }
        ReservationRequestStored event = storageResult.get();

        return reservationRequester.append(event.reservationRequest())
                .onFailure(exception -> log.error("cannot store reservation request, reason: {}", exception.getMessage()))
                .onSuccess(reservationRequest -> {
                    reservationRequesterRepository.save(reservationRequester);
                    parkingSpotReservationRequestsRepository.publish(event);
                });
    }

    private ReservationRequester findReservationRequesterBy(ReservationRequesterId requesterId) {
        return reservationRequesterRepository.findBy(requesterId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find reservation requester with id: " + requesterId));
    }

    private ParkingSpotReservationRequests findParkingSpotReservationRequestsBy(ParkingSpotTimeSlotId parkingSpotTimeSlotId) {
        return parkingSpotReservationRequestsRepository.findBy(parkingSpotTimeSlotId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot reservation requests with id: " + parkingSpotTimeSlotId));
    }

    private ParkingSpotReservationRequests findParkingSpotReservationRequestsBy(ParkingSpotCategory parkingSpotCategory, TimeSlot timeSlot) {
        return parkingSpotReservationRequestsRepository.findBy(parkingSpotCategory, timeSlot)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot reservation requests for category: " + parkingSpotCategory + " and time slot: " + timeSlot));
    }

}
