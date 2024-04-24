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
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId;
import pl.cezarysanecki.parkingdomain.requesting.view.client.model.ClientReservationRequestsView;
import pl.cezarysanecki.parkingdomain.requesting.view.client.model.ClientReservationRequestsViews;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
class ClientReservationRequestsController {

    private final ClientReservationRequestsViews clientReservationRequestsViews;
    private final MakingRequestForPartOfParkingSpot makingRequestForPartOfParkingSpot;
    private final MakingRequestForWholeParkingSpot makingRequestForWholeParkingSpot;
    private final CancellingRequest cancellingRequest;

    @GetMapping("/client-reservation-requests/{clientId}")
    ResponseEntity<ClientReservationRequestsView> getClientReservations(@PathVariable UUID clientId) {
        ClientReservationRequestsView clientReservationRequestsView = clientReservationRequestsViews.getClientReservationRequestsViewFor(ClientId.of(clientId));
        return ResponseEntity.ok(clientReservationRequestsView);
    }

    @PostMapping("/client-reservation-requests/{clientId}/part-of-parking-spot")
    ResponseEntity requestsReservationOnPartOfParkingSpot(@PathVariable UUID clientId, @RequestBody RequestReservationOnPartOfParkingSpotRequest request) {
        Try<Result> result = makingRequestForPartOfParkingSpot.makeRequest(
                new MakingRequestForPartOfParkingSpot.Command(
                        ClientId.of(clientId),
                        ParkingSpotId.of(request.parkingSpotId),
                        VehicleSize.of(request.vehicleSize)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/client-reservation-requests/{clientId}/whole-parking-spot")
    ResponseEntity requestsReservationOnWholeParkingSpot(@PathVariable UUID clientId, @RequestBody RequestReservationOnWholeParkingSpotRequest request) {
        Try<Result> result = makingRequestForWholeParkingSpot.makeRequest(
                new MakingRequestForWholeParkingSpot.Command(
                        ClientId.of(clientId),
                        ParkingSpotId.of(request.parkingSpotId)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/client-reservation-requests/{reservationId}")
    ResponseEntity cancel(@PathVariable UUID reservationId) {
        Try<Result> result = cancellingRequest.cancelRequest(
                new CancellingRequest.Command(ReservationId.of(reservationId)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

}

@Data
class RequestReservationOnPartOfParkingSpotRequest {
    UUID parkingSpotId;
    Integer vehicleSize;
}

@Data
class RequestReservationOnWholeParkingSpotRequest {
    UUID parkingSpotId;
}
