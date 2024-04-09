package pl.cezarysanecki.parkingdomain.reserving.view.client.web;

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
import pl.cezarysanecki.parkingdomain.reserving.client.application.CancellingReservationRequest;
import pl.cezarysanecki.parkingdomain.reserving.client.application.ReservingPartOfParkingSpot;
import pl.cezarysanecki.parkingdomain.reserving.client.application.ReservingWholeParkingSpot;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reserving.view.client.model.ClientReservationsView;
import pl.cezarysanecki.parkingdomain.reserving.view.client.model.ClientReservationsViews;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
class ClientReservationsController {

    private final ClientReservationsViews clientReservationsViews;
    private final ReservingPartOfParkingSpot reservingPartOfParkingSpot;
    private final ReservingWholeParkingSpot reservingWholeParkingSpot;
    private final CancellingReservationRequest cancellingReservationRequest;

    @GetMapping("/client-reservations/{clientId}")
    ResponseEntity<ClientReservationsView> getClientReservations(@PathVariable UUID clientId) {
        ClientReservationsView clientReservationsView = clientReservationsViews.getClientReservationsViewFor(ClientId.of(clientId));
        return ResponseEntity.ok(clientReservationsView);
    }

    @PostMapping("/client-reservations/{clientId}/part-parking-spot")
    ResponseEntity reservePartOfParkingSpot(@PathVariable UUID clientId, @RequestBody ReservePartOfParkingSpotRequest request) {
        Try<Result> result = reservingPartOfParkingSpot.requestReservation(
                new ReservingPartOfParkingSpot.Command(
                        ClientId.of(clientId),
                        ParkingSpotId.of(request.parkingSpotId),
                        VehicleSize.of(request.vehicleSize)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/client-reservations/{clientId}/whole-parking-spot")
    ResponseEntity reserveWholeParkingSpot(@PathVariable UUID clientId, @RequestBody ReserveWholeParkingSpotRequest request) {
        Try<Result> result = reservingWholeParkingSpot.requestReservation(
                new ReservingWholeParkingSpot.Command(
                        ClientId.of(clientId),
                        ParkingSpotId.of(request.parkingSpotId)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/client-reservations/reservation/{reservationId}")
    ResponseEntity cancel(@PathVariable UUID reservationId) {
        Try<Result> result = cancellingReservationRequest.cancelReservationRequest(
                new CancellingReservationRequest.Command(ReservationId.of(reservationId)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

}

@Data
class ReservePartOfParkingSpotRequest {
    UUID parkingSpotId;
    Integer vehicleSize;
}

@Data
class ReserveWholeParkingSpotRequest {
    UUID parkingSpotId;
}