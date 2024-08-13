package pl.cezarysanecki.parkingdomain.web;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotFacade;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade;
import pl.cezarysanecki.parkingdomain.parking.api.Occupant;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
class ParkingController {

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
    boolean result = parkingFacade.occupy(
        new Occupant(request.occupant),
        new ParkingSpotId(request.parkingSpotId),
        new SpotUnits(request.spotUnits)
    );
    return result ? ResponseEntity.ok().build() : ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
  }

  @PostMapping("/occupy-whole")
  ResponseEntity occupyWholeParkingSpot(@RequestBody OccupyWholeParkingSpotRequest request) {
    boolean result = parkingFacade.occupyWhole(
        new Occupant(request.occupant),
        new ParkingSpotId(request.parkingSpotId)
    );
    return result ? ResponseEntity.ok().build() : ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
  }

  @DeleteMapping("/release")
  ResponseEntity releaseParkingSpot(@RequestBody ReleaseParkingSpotRequest request) {
    boolean result = parkingFacade.release(new OccupationId(request.occupationId));
    return result ? ResponseEntity.ok().build() : ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
  }

  record CreateParkingSpotRequest(
      ParkingSpotCategory category
  ) {
  }

  record OccupyParkingSpotRequest(
      UUID occupant,
      UUID parkingSpotId,
      int spotUnits
  ) {
  }

  record OccupyWholeParkingSpotRequest(
      UUID occupant,
      UUID parkingSpotId
  ) {
  }

  record ReleaseParkingSpotRequest(
      UUID occupationId
  ) {
  }

}
