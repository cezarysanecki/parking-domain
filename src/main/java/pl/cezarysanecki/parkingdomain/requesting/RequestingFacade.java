package pl.cezarysanecki.parkingdomain.requesting;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.requesting.api.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.Instant;

@RequiredArgsConstructor
public class RequestingFacade {

  private final ReservationRequestsRepository reservationRequestsRepository;
  private final ReservationRequesterRepository reservationRequesterRepository;

  @Transactional
  public void create(
      ParkingSpot parkingSpot,
      TimeSlot timeSlot
  ) {
    if (reservationRequestsRepository.exists(parkingSpot.parkingSpotId(), timeSlot)) {
      return;
    }
    RequestableParkingSpotSectionsGrouped requestableParkingSpotSectionsGrouped = RequestableParkingSpotSectionsGrouped.of(
        parkingSpot, timeSlot);
    reservationRequestsRepository.saveNew(requestableParkingSpotSectionsGrouped);
  }

  @Transactional
  public boolean request(
      RequesterId requesterId,
      ParkingSpotId parkingSpotId,
      TimeSlot timeSlot,
      SpotUnits spotUnits
  ) {
    RequestableParkingSpotSectionsGrouped requestableParkingSpotSectionsGrouped = reservationRequestsRepository.findBy(
        parkingSpotId, timeSlot, spotUnits);
    ReservationRequester requester = reservationRequesterRepository.findBy(requesterId);

    ReservationRequestId reservationRequestId = ReservationRequestId.newOne();
    if (!requestableParkingSpotSectionsGrouped.request(reservationRequestId)
        || !requester.append(reservationRequestId)) {
      return false;
    }
    reservationRequestsRepository.saveCheckingVersion(requestableParkingSpotSectionsGrouped);
    reservationRequesterRepository.saveCheckingVersion(requester);
    return true;
  }

  @Transactional
  public boolean cancel(
      ReservationRequestId reservationRequestId
  ) {
    RequestableParkingSpotSectionsGrouped requestableParkingSpotSectionsGrouped = reservationRequestsRepository.findBy(
        reservationRequestId);
    ReservationRequester requester = reservationRequesterRepository.findBy(reservationRequestId);

    if (!requestableParkingSpotSectionsGrouped.release(reservationRequestId)
        || !requester.cancel(reservationRequestId)) {
      return false;
    }
    reservationRequestsRepository.saveCheckingVersion(requestableParkingSpotSectionsGrouped);
    reservationRequesterRepository.saveCheckingVersion(requester);
    return true;
  }

  @Transactional
  public boolean makeAllValid(
      Instant date
  ) {


    return makingReservationRequest.makeRequest(requesterId, timeSlotId, spotUnits);
  }

}
