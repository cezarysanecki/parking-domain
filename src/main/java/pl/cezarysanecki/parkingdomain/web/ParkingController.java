package pl.cezarysanecki.parkingdomain.web;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.management.client.api.PhoneNumber;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotFacade;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.usecase.OccupyUsingReservationUseCase;
import pl.cezarysanecki.parkingdomain.parking.usecase.OccupyingWithoutAccountUseCase;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotForceReleased;
import pl.cezarysanecki.parkingdomain.parking.usecase.RemoveOccupationByForceUseCase;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
class ParkingController {

  private final OccupyingWithoutAccountUseCase occupyingWithoutAccountUseCase;
  private final RemoveOccupationByForceUseCase removeOccupationByForceUseCase;
  private final OccupyUsingReservationUseCase occupyUsingReservationUseCase;
  private final ParkingSpotFacade parkingSpotFacade;
  private final ParkingFacade parkingFacade;

  @PostMapping("/add")
  ResponseEntity createParkingSpot(@RequestBody CreateParkingSpotRequest request) {
    Try<ParkingSpotId> result = parkingSpotFacade.addParkingSpot(
        ParkingSpotCapacity.defaultCapacity(),
        request.category
    );
    return result
        .map(success -> ResponseEntity.ok().build())
        .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
  }

  @PostMapping("/occupy")
  ResponseEntity occupyParkingSpot(@RequestBody OccupyParkingSpotRequest request) {
    var result = parkingFacade.occupy(
        new OccupantId(request.occupantId),
        new ParkingSpotId(request.parkingSpotId),
        new SpotUnits(request.spotUnits)
    );
    return result
        .map(OccupationId::toString)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.internalServerError().build());
  }

  @PostMapping("/occupy-without-account")
  ResponseEntity occupyParkingSpotWihtoutAccount(@RequestBody OccupyParkingSpotWithoutAccountRequest request) {
    var result = occupyingWithoutAccountUseCase.run(
        PhoneNumber.of(request.phoneNumber),
        new ParkingSpotId(request.parkingSpotId),
        new SpotUnits(request.spotUnits)
    );

    return result
        .map(OccupationId::toString)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.internalServerError().build());
  }

  @PostMapping("/occupy-with-reservation")
  ResponseEntity occupyParkingSpotWithReservation(@RequestBody OccupyParkingSpotUsingReservationRequest request) {
    var result = occupyUsingReservationUseCase.run(
        new OccupantId(request.occupantId),
        new ReservationId(request.reservationId)
    );
    return result
        .map(OccupationId::toString)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.internalServerError().build());
  }

  @DeleteMapping("/release")
  ResponseEntity releaseParkingSpot(@RequestBody ReleaseParkingSpotRequest request) {
    var result = parkingFacade.release(new OccupationId(request.occupationId));
    return result.isPresent() ? ResponseEntity.ok().build() : ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
  }

  @DeleteMapping("/release-force")
  ResponseEntity releaseParkingSpotByForce(@RequestBody ReleaseByForceParkingSpotRequest request) {
    boolean result = removeOccupationByForceUseCase.run(
        new OccupationId(request.occupationId),
        ParkingSpotForceReleased.Reason.NOT_RELEASED_PARKING_SPOT
    );
    return result ? ResponseEntity.ok().build() : ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
  }

  record CreateParkingSpotRequest(
      ParkingSpotCategory category
  ) {
  }

  record OccupyParkingSpotRequest(
      UUID occupantId,
      UUID parkingSpotId,
      int spotUnits
  ) {
  }

  record OccupyParkingSpotWithoutAccountRequest(
      String phoneNumber,
      UUID parkingSpotId,
      int spotUnits
  ) {
  }

  record OccupyParkingSpotUsingReservationRequest(
      UUID occupantId,
      UUID reservationId
  ) {
  }

  record ReleaseParkingSpotRequest(
      UUID occupationId
  ) {
  }

  record ReleaseByForceParkingSpotRequest(
      UUID occupationId
  ) {
  }

}
