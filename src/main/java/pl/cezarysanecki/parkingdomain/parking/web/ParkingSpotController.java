package pl.cezarysanecki.parkingdomain.parking.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.ParkingSpotFacade;
import pl.cezarysanecki.parkingdomain.parking.api.Occupant;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
class ParkingSpotController {

  private final ParkingSpotFacade parkingSpotFacade;

  @PostMapping("/parking-spot")
  ResponseEntity create() {
    ParkingSpotId parkingSpotId = parkingSpotFacade.create();
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(parkingSpotId);
  }

  @PostMapping("/parking-spot/occupy")
  ResponseEntity occupy(@RequestBody OccupyParkingSpotRequest request) {
    boolean result = parkingSpotFacade.occupy(
        new Occupant(request.occupant),
        new ParkingSpotId(request.parkingSpotId),
        new SpotUnits(request.units));

    return result ?
        ResponseEntity.ok().build() :
        ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
  }

  @PostMapping("/parking-spot/occupy-whole")
  ResponseEntity occupyWhole(@RequestBody OccupyWholeParkingSpotRequest request) {
    boolean result = parkingSpotFacade.occupyWhole(
        new Occupant(request.occupant),
        new ParkingSpotId(request.parkingSpotId));

    return result ?
        ResponseEntity.ok().build() :
        ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
  }

  @DeleteMapping("/parking-spot/release")
  ResponseEntity release(@RequestBody ReleaseParkingSpotRequest request) {
    boolean result = parkingSpotFacade.release(new Occupant(request.occupant));

    return result ?
        ResponseEntity.ok().build() :
        ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
  }

  record OccupyParkingSpotRequest(
      UUID occupant,
      UUID parkingSpotId,
      int units) {
  }

  record OccupyWholeParkingSpotRequest(
      UUID occupant,
      UUID parkingSpotId) {
  }

  record ReleaseParkingSpotRequest(
      UUID occupant) {
  }

}
