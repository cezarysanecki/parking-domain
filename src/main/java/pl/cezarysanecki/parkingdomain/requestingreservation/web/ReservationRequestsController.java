package pl.cezarysanecki.parkingdomain.requestingreservation.web;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.CancellingReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.MakingReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.ok;
import static pl.cezarysanecki.parkingdomain.requestingreservation.web.ReservationRequestsViewRepository.ParkingSpotReservationRequestsView;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
class ReservationRequestsController {

  private final ReservationRequestsViewRepository reservationRequestsViewRepository;
  private final MakingReservationRequest makingReservationRequest;
  private final CancellingReservationRequest cancellingReservationRequest;

  @GetMapping("/parking-spots/available")
  ResponseEntity<List<ParkingSpotReservationRequestsView>> availableParkingSpots() {
    return ok(reservationRequestsViewRepository.queryForAllAvailableParkingSpots());
  }

  @PostMapping("/parking-spot/store")
  ResponseEntity storeRequest(@RequestBody StoreReservationRequestRequest request) {
    Try<ReservationRequest> result = makingReservationRequest.makeRequest(
        ReservationRequesterId.of(request.requesterId),
        ReservationRequestsTimeSlotId.of(request.reservationRequestsTimeSlotId),
        SpotUnits.of(request.units));
    return result
        .map(success -> ResponseEntity.ok().build())
        .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
  }

  @DeleteMapping("/parking-spot/cancel")
  ResponseEntity cancel(@RequestBody CancelReservationRequestRequest request) {
    Try<ReservationRequest> result = cancellingReservationRequest.cancelRequest(
        ReservationRequestId.of(request.reservationRequestId));
    return result
        .map(success -> ResponseEntity.ok().build())
        .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
  }

  record StoreReservationRequestRequest(
      UUID requesterId,
      UUID reservationRequestsTimeSlotId,
      int units) {
  }

  record CancelReservationRequestRequest(
      UUID reservationRequestId) {
  }

}
