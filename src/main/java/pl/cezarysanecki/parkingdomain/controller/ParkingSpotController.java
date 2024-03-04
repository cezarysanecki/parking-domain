package pl.cezarysanecki.parkingdomain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.model.Vehicle;
import pl.cezarysanecki.parkingdomain.service.ParkingSpotService;
import pl.cezarysanecki.parkingdomain.service.VehicleService;

import java.util.List;

@RestController
@RequestMapping("/parking-spot")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;
    private final VehicleService vehicleService;

    @PostMapping
    public Long create() {
        return parkingSpotService.create().getId();
    }

    @PostMapping("/{id}/release")
    public Long release(
            @PathVariable("id") Long id
    ) {
        return parkingSpotService.release(id).getId();
    }

    @PostMapping("/{parkingSpotId}/reserve-for/{vehicleId}")
    public Long reserve(
            @PathVariable("parkingSpotId") Long parkingSpotId,
            @PathVariable("vehicleId") Long vehicleId
    ) {
        Vehicle vehicle = vehicleService.findBy(vehicleId);
        return parkingSpotService.reservedFor(parkingSpotId, vehicle).getId();
    }

    @PostMapping("/reserve-any/{vehicleId}")
    public Long reserveAny(
            @PathVariable("vehicleId") Long vehicleId
    ) {
        Vehicle vehicle = vehicleService.findBy(vehicleId);
        return parkingSpotService.reservedAnyFor(vehicle).getId();
    }

    @GetMapping("/{id}")
    public ParkingSpot findBy(
            @PathVariable("id") Long id
    ) {
        return parkingSpotService.findBy(id);
    }

    @GetMapping
    public List<ParkingSpot> findAll() {
        return parkingSpotService.findAll();
    }

}
