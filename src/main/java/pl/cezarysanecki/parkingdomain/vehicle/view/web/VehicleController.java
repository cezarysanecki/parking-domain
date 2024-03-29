package pl.cezarysanecki.parkingdomain.vehicle.view.web;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.vehicle.parking.application.DrivingVehicleAway;
import pl.cezarysanecki.parkingdomain.vehicle.parking.application.ParkingVehicle;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.vehicle.view.model.VehicleView;
import pl.cezarysanecki.parkingdomain.vehicle.view.model.VehicleViews;

import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
class VehicleController {

    private final VehicleViews vehicleViews;
    private final ParkingVehicle parkingVehicle;
    private final DrivingVehicleAway drivingVehicleAway;

    @GetMapping("/vehicle/parked")
    ResponseEntity<Set<VehicleView>> parkedVehicles() {
        return ok(vehicleViews.queryForParkedVehicles());
    }

    @GetMapping("/vehicle/not-parked")
    ResponseEntity<Set<VehicleView>> notParkedVehicles() {
        return ok(vehicleViews.queryForNotParkedVehicles());
    }

    @GetMapping("/vehicle")
    ResponseEntity<Set<VehicleView>> allVehicles() {
        return ok(vehicleViews.queryForAllVehicles());
    }

    @PostMapping("/vehicle/{vehicleId}/park-on/{parkingSpotId}")
    ResponseEntity create(@PathVariable UUID vehicleId, @PathVariable UUID parkingSpotId) {
        Try<Result> result = parkingVehicle.park(new ParkingVehicle.Command(
                VehicleId.of(vehicleId),
                ParkingSpotId.of(parkingSpotId)
        ));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/vehicle/{vehicleId}/drive-away")
    ResponseEntity create(@PathVariable UUID vehicleId) {
        Try<Result> result = drivingVehicleAway.driveAway(new DrivingVehicleAway.Command(
                VehicleId.of(vehicleId)
        ));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

}
