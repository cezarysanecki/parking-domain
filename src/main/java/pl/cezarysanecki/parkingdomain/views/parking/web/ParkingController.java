package pl.cezarysanecki.parkingdomain.views.parking.web;

import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.application.parking.ParkReservedVehicleCommand;
import pl.cezarysanecki.parkingdomain.parking.application.parking.ParkVehicleCommand;
import pl.cezarysanecki.parkingdomain.parking.application.parking.ParkingOnParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.releasing.ReleaseParkingSpotCommand;
import pl.cezarysanecki.parkingdomain.parking.application.releasing.ReleasingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.views.parking.model.AvailableParkingSpotView;
import pl.cezarysanecki.parkingdomain.views.parking.model.ParkingViews;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
class ParkingController {

    private final ParkingOnParkingSpot parkingOnParkingSpot;
    private final ReleasingParkingSpot releasingParkingSpot;
    private final ParkingViews parkingViews;

    @PostMapping("/park")
    ResponseEntity park(@RequestBody ParkVehicleRequest request) {
        Try<Result> result = parkingOnParkingSpot.park(new ParkVehicleCommand(
                request.parkingSpotType,
                new Vehicle(VehicleId.of(request.vehicleId), VehicleSizeUnit.of(request.vehicleSize))));

        return result
                .map(success -> switch (success) {
                    case Success -> ResponseEntity.ok().build();
                    case Rejection -> ResponseEntity.badRequest().build();
                })
                .getOrElse(() -> ResponseEntity.internalServerError().build());
    }

    @PostMapping("/park/reservation")
    ResponseEntity parkOnReserved(@RequestBody ParkVehicleUsingReservationRequest request) {
        Try<Result> result = parkingOnParkingSpot.park(new ParkReservedVehicleCommand(
                ReservationId.of(request.reservationId),
                new Vehicle(VehicleId.of(request.vehicleId), VehicleSizeUnit.of(request.vehicleSize))));

        return result
                .map(success -> switch (success) {
                    case Success -> ResponseEntity.ok().build();
                    case Rejection -> ResponseEntity.badRequest().build();
                })
                .getOrElse(() -> ResponseEntity.internalServerError().build());
    }

    @DeleteMapping("/drive-away/{vehicleId}")
    ResponseEntity driveAway(@PathVariable UUID vehicleId) {
        Try<Result> result = releasingParkingSpot.release(new ReleaseParkingSpotCommand(VehicleId.of(vehicleId)));

        return result
                .map(success -> switch (success) {
                    case Success -> ResponseEntity.ok().build();
                    case Rejection -> ResponseEntity.badRequest().build();
                })
                .getOrElse(() -> ResponseEntity.internalServerError().build());
    }

    @GetMapping("/available-parking-spots")
    ResponseEntity<Set<AvailableParkingSpot>> getAvailableParkingSpots() {
        return ResponseEntity.ok(
                parkingViews.findAvailable()
                        .getAvailableParkingSpots()
                        .stream()
                        .map(AvailableParkingSpot::new)
                        .collect(Collectors.toUnmodifiableSet()));
    }

}

@Getter
@NoArgsConstructor
@AllArgsConstructor
class ParkVehicleRequest {

    ParkingSpotType parkingSpotType;
    UUID vehicleId;
    int vehicleSize;

}

@Getter
@NoArgsConstructor
@AllArgsConstructor
class ParkVehicleUsingReservationRequest {

    UUID reservationId;
    UUID vehicleId;
    int vehicleSize;

}

@Getter
@NoArgsConstructor
@AllArgsConstructor
class LeaveParkingSpotRequest {

    UUID vehicleId;

}

@Getter
@NoArgsConstructor
class AvailableParkingSpot {

    UUID parkingSpotId;
    ParkingSpotType parkingSpotType;
    int leftCapacity;

    AvailableParkingSpot(AvailableParkingSpotView availableParkingSpotView) {
        this.parkingSpotId = availableParkingSpotView.getParkingSpotId().getValue();
        this.parkingSpotType = availableParkingSpotView.
        this.leftCapacity = availableParkingSpotView.getLeftCapacity();
    }

}
