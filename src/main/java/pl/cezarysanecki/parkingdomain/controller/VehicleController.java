package pl.cezarysanecki.parkingdomain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.api.ParkedVehicleResource;
import pl.cezarysanecki.parkingdomain.model.Vehicle;
import pl.cezarysanecki.parkingdomain.model.VehicleType;
import pl.cezarysanecki.parkingdomain.service.VehicleService;

import java.util.List;

@RestController
@RequestMapping("/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/{vehicleType}")
    public Long create(
            @PathVariable("vehicleType") VehicleType vehicleType
    ) {
        return vehicleService.create(vehicleType).getId();
    }

    @PostMapping("/{vehicleId}/park-on/{parkingSpotId}")
    public Long park(
            @PathVariable("vehicleId") Long vehicleId,
            @PathVariable("parkingSpotId") Long parkingSpotId
    ) {
        Vehicle vehicle = vehicleService.findBy(vehicleId);
        return vehicleService.park(parkingSpotId, vehicle).getId();
    }

    @PostMapping("/{vehicleId}/park-anywhere")
    public Long parkAnywhere(
            @PathVariable("vehicleId") Long vehicleId
    ) {
        Vehicle vehicle = vehicleService.findBy(vehicleId);
        return vehicleService.parkAnywhere(vehicle).getId();
    }

    @PostMapping("/{vehicleId}/park-anywhere/test")
    public ParkedVehicleResource parkAnywhereWithResource(
            @PathVariable("vehicleId") Long vehicleId
    ) {
        Vehicle vehicle = vehicleService.findBy(vehicleId);
        Vehicle parkedVehicle = vehicleService.parkAnywhere(vehicle);

        return new ParkedVehicleResource(
                parkedVehicle.getId(),
                parkedVehicle.getParkingSpot().getId());
    }

    @GetMapping("/{id}")
    public Vehicle findBy(
            @PathVariable("id") Long id
    ) {
        return vehicleService.findBy(id);
    }

    @GetMapping
    public List<Vehicle> findAll() {
        return vehicleService.findAll();
    }

}
