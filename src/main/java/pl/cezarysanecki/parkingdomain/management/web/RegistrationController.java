package pl.cezarysanecki.parkingdomain.management.web;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.management.client.ClientType;
import pl.cezarysanecki.parkingdomain.management.client.RegisteringClient;
import pl.cezarysanecki.parkingdomain.management.parkingspot.AddingParkingSpot;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
class RegistrationController {

    private final AddingParkingSpot addingParkingSpot;
    private final RegisteringClient registeringClient;

    @PostMapping("/parking-spot")
    ResponseEntity addParkingSpot(@RequestBody AddParkingSpotRequest request) {
        Try<Result> result = addingParkingSpot.addParkingSpot(request.capacity, request.category);
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/client")
    ResponseEntity registerClient(@RequestBody RegisterClientRequest request) {
        Try<Result> result = registeringClient.registerClient(request.clientType, request.phoneNumber);
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
