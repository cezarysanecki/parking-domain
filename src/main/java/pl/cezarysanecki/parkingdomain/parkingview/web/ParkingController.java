package pl.cezarysanecki.parkingdomain.parkingview.web;

import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.application.ParkVehicleCommand;
import pl.cezarysanecki.parkingdomain.parking.application.ParkingOnParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.parkingview.model.AvailableParkingSpotsView;
import pl.cezarysanecki.parkingdomain.parkingview.model.ParkingViews;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
class ParkingController {

    private final ParkingOnParkingSpot parkingOnParkingSpot;
    private final ParkingViews parkingViews;

    @PostMapping("/park-on/{parkingSpotId}")
    ResponseEntity park(@PathVariable UUID parkingSpotId, @RequestBody ParkVehicleRequest request) {
        Try<Result> result = parkingOnParkingSpot.park(new ParkVehicleCommand(
                ParkingSpotId.of(parkingSpotId),
                new Vehicle(VehicleId.of(request.vehicleId), VehicleSizeUnit.of(request.vehicleSize)),
                Instant.now()));

        return result
                .map(success -> switch (success) {
                    case Success -> ResponseEntity.ok().build();
                    case Rejection -> ResponseEntity.badRequest().build();
                })
                .getOrElse(ResponseEntity.internalServerError().build());
    }

    @GetMapping("/available-parking-spots")
    ResponseEntity<Set<AvailableParkingSpot>> getAvailableParkingSpots() {
        AvailableParkingSpotsView available = parkingViews.findAvailable();

        return ResponseEntity.ok(
                available.getAvailableParkingSpots()
                        .stream()
                        .map(availableParkingSpotView -> new AvailableParkingSpot(
                                availableParkingSpotView.getParkingSpotId().getValue(),
                                availableParkingSpotView.getLeftCapacity()
                        ))
                        .collect(Collectors.toUnmodifiableSet()));
    }

}

@Getter
@NoArgsConstructor
@AllArgsConstructor
class ParkVehicleRequest {

    UUID vehicleId;
    int vehicleSize;

}

@Getter
@NoArgsConstructor
@AllArgsConstructor
class AvailableParkingSpot {

    UUID parkingSpotId;
    int leftCapacity;

}
