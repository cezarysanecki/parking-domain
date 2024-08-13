package pl.cezarysanecki.parkingdomain.web;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType;
import pl.cezarysanecki.parkingdomain.management.client.ClientFacade;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotFacade;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
class RegistrationController {

  private final ParkingSpotFacade parkingSpotFacade;
  private final ClientFacade clientFacade;

  @PostMapping("/parking-spot")
  ResponseEntity addParkingSpot(@RequestBody AddParkingSpotRequest request) {
    Try<ParkingSpotId> result = parkingSpotFacade.addParkingSpot(request.capacity, request.category);
    return result
        .map(success -> ResponseEntity.ok().build())
        .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
  }

  @PostMapping("/client")
  ResponseEntity registerClient(@RequestBody RegisterClientRequest request) {
    Try<ClientId> result = clientFacade.registerClient(request.clientType, request.phoneNumber);
    return result
        .map(success -> ResponseEntity.ok().build())
        .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
  }

  record AddParkingSpotRequest(
      int capacity,
      ParkingSpotCategory category) {
  }

  record RegisterClientRequest(
      ClientType clientType,
      String phoneNumber) {
  }

}
