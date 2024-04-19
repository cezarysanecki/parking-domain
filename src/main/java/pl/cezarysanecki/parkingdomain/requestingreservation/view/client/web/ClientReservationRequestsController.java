package pl.cezarysanecki.parkingdomain.requestingreservation.view.client.web;

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
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.CancellingReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.RequestingReservationForPartOfParkingSpot;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.RequestingReservationForWholeParkingSpot;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.client.model.ClientReservationRequestsView;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.client.model.ClientReservationRequestsViews;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
class ClientReservationRequestsController {

    private final ClientReservationRequestsViews clientReservationRequestsViews;
    private final RequestingReservationForPartOfParkingSpot requestingReservationForPartOfParkingSpot;
    private final RequestingReservationForWholeParkingSpot requestingReservationForWholeParkingSpot;
    private final CancellingReservationRequest cancellingReservationRequest;

    @GetMapping("/client-reservation-requests/{clientId}")
    ResponseEntity<ClientReservationRequestsView> getClientReservations(@PathVariable UUID clientId) {
        ClientReservationRequestsView clientReservationRequestsView = clientReservationRequestsViews.getClientReservationRequestsViewFor(ClientId.of(clientId));
        return ResponseEntity.ok(clientReservationRequestsView);
    }

    @PostMapping("/client-reservation-requests/{clientId}/part-of-parking-spot")
    ResponseEntity requestsReservationForPartOfParkingSpot(@PathVariable UUID clientId, @RequestBody RequestReservationForPartOfParkingSpotRequest request) {
        Try<Result> result = requestingReservationForPartOfParkingSpot.requestReservation(
                new RequestingReservationForPartOfParkingSpot.Command(
                        ClientId.of(clientId),
                        ParkingSpotId.of(request.parkingSpotId),
                        VehicleSize.of(request.vehicleSize)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/client-reservation-requests/{clientId}/whole-parking-spot")
    ResponseEntity requestsReservationForWholeParkingSpot(@PathVariable UUID clientId, @RequestBody RequestReservationForWholeParkingSpotRequest request) {
        Try<Result> result = requestingReservationForWholeParkingSpot.requestReservation(
                new RequestingReservationForWholeParkingSpot.Command(
                        ClientId.of(clientId),
                        ParkingSpotId.of(request.parkingSpotId)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/client-reservation-requests/{reservationId}")
    ResponseEntity cancel(@PathVariable UUID reservationId) {
        Try<Result> result = cancellingReservationRequest.cancelReservationRequest(
                new CancellingReservationRequest.Command(ReservationId.of(reservationId)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

}

@Data
class RequestReservationForPartOfParkingSpotRequest {
    UUID parkingSpotId;
    Integer vehicleSize;
}

@Data
class RequestReservationForWholeParkingSpotRequest {
    UUID parkingSpotId;
}
