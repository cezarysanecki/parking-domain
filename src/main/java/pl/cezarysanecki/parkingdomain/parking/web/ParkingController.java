package pl.cezarysanecki.parkingdomain.parking.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import org.springframework.http.ResponseEntity;
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

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class ParkingController {

    private final ParkingOnParkingSpot parkingOnParkingSpot;

    @PostMapping("/parking/{parkingSpotId}")
    ResponseEntity park(@PathVariable UUID parkingSpotId, @RequestBody ParkVehicleRequest request) {
        Try<Result> result = parkingOnParkingSpot.park(new ParkVehicleCommand(
                ParkingSpotId.of(parkingSpotId),
                new Vehicle(VehicleId.of(request.vehicleId), VehicleSizeUnit.of(request.vehicleSize)),
                Instant.now()
        ));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.internalServerError().build());
    }

}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ParkVehicleRequest {
    UUID vehicleId;
    int vehicleSize;
}
