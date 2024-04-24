package pl.cezarysanecki.parkingdomain.requesting.view.client.web;

import io.vavr.control.Try;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.requesting.client.application.CancellingRequest;
import pl.cezarysanecki.parkingdomain.requesting.client.application.MakingRequestForPartOfParkingSpot;
import pl.cezarysanecki.parkingdomain.requesting.client.application.MakingRequestForWholeParkingSpot;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.view.client.model.ClientRequestsView;
import pl.cezarysanecki.parkingdomain.requesting.view.client.model.ClientRequestsViews;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
class ClientRequestsController {

    private final ClientRequestsViews clientRequestsViews;
    private final MakingRequestForPartOfParkingSpot makingRequestForPartOfParkingSpot;
    private final MakingRequestForWholeParkingSpot makingRequestForWholeParkingSpot;
    private final CancellingRequest cancellingRequest;

    @GetMapping("/client-requests/{clientId}")
    ResponseEntity<ClientRequestsView> getClientRequests(@PathVariable UUID clientId) {
        ClientRequestsView clientRequestsView = clientRequestsViews.getClientRequestsViewFor(ClientId.of(clientId));
        return ResponseEntity.ok(clientRequestsView);
    }

    @PostMapping("/client-requests/{clientId}/part-of-parking-spot")
    ResponseEntity requestPartOfParkingSpot(@PathVariable UUID clientId, @RequestBody RequestPartOfParkingSpotRequest request) {
        Try<Result> result = makingRequestForPartOfParkingSpot.makeRequest(
                new MakingRequestForPartOfParkingSpot.Command(
                        ClientId.of(clientId),
                        ParkingSpotId.of(request.parkingSpotId),
                        VehicleSize.of(request.vehicleSize)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/client-requests/{clientId}/whole-parking-spot")
    ResponseEntity requestWholeParkingSpot(@PathVariable UUID clientId, @RequestBody RequestWholeParkingSpotRequest request) {
        Try<Result> result = makingRequestForWholeParkingSpot.makeRequest(
                new MakingRequestForWholeParkingSpot.Command(
                        ClientId.of(clientId),
                        ParkingSpotId.of(request.parkingSpotId)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/client-requests/{requestId}")
    ResponseEntity cancel(@PathVariable UUID requestId) {
        Try<Result> result = cancellingRequest.cancelRequest(
                new CancellingRequest.Command(RequestId.of(requestId)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

}

@Data
class RequestPartOfParkingSpotRequest {
    UUID parkingSpotId;
    Integer vehicleSize;
}

@Data
class RequestWholeParkingSpotRequest {
    UUID parkingSpotId;
}
