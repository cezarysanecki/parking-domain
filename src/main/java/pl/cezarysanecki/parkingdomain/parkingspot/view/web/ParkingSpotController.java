package pl.cezarysanecki.parkingdomain.parkingspot.view.web;

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
import pl.cezarysanecki.parkingdomain.parkingspot.parking.application.CreatingParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.application.OccupyingParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.application.ReleasingParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parkingspot.view.model.ParkingSpotView;
import pl.cezarysanecki.parkingdomain.parkingspot.view.model.ParkingSpotViews;
import pl.cezarysanecki.parkingdomain.vehicle.parking.VehicleId;
import pl.cezarysanecki.parkingdomain.vehicle.parking.VehicleInformation;
import pl.cezarysanecki.parkingdomain.vehicle.parking.VehicleSize;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleInformation;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleSize;

import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
class ParkingSpotController {

    private final CreatingParkingSpot creatingParkingSpot;
    private final OccupyingParkingSpot occupyingParkingSpot;
    private final ReleasingParkingSpot releasingParkingSpot;
    private final ParkingSpotViews parkingSpotViews;

    @GetMapping("/parking-spot/available")
    ResponseEntity<Set<ParkingSpotView>> availableParkingSpots() {
        return ok(parkingSpotViews.queryForAvailableParkingSpots());
    }

    @PostMapping("/parking-spot/create")
    ResponseEntity create(@RequestBody CreateParkingSpotRequest request) {
        Try<Result> result = creatingParkingSpot.create(new CreatingParkingSpot.Command(
                ParkingSpotCapacity.of(request.capacity)
        ));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/parking-spot/{parkingSpotId}/occupy")
    ResponseEntity occupy(@PathVariable UUID parkingSpotId, @RequestBody OccupyParkingSpotRequest request) {
        Try<Result> result = occupyingParkingSpot.occupy(new OccupyingParkingSpot.Command(
                ParkingSpotId.of(parkingSpotId),
                VehicleInformation.of(
                        VehicleId.of(request.vehicleId),
                        VehicleSize.of(request.size))
        ));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/parking-spot/drive-away/{vehicleId}")
    ResponseEntity occupy(@PathVariable UUID vehicleId) {
        Try<Result> result = releasingParkingSpot.driveAway(new ReleasingParkingSpot.Command(
                VehicleId.of(vehicleId)
        ));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

}

@Data
class CreateParkingSpotRequest {
    Integer capacity;
}

@Data
class OccupyParkingSpotRequest {
    UUID vehicleId;
    Integer size;
}
