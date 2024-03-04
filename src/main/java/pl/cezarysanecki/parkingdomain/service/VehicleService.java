package pl.cezarysanecki.parkingdomain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cezarysanecki.parkingdomain.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.model.ParkingSpotStatus;
import pl.cezarysanecki.parkingdomain.model.Vehicle;
import pl.cezarysanecki.parkingdomain.model.VehicleType;
import pl.cezarysanecki.parkingdomain.repository.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.repository.VehicleRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ParkingSpotService parkingSpotService;
    private final ParkingSpotRepository parkingSpotRepository;

    public Vehicle create(VehicleType vehicleType) {
        Vehicle vehicle = new Vehicle();
        vehicle.setType(vehicleType);
        return vehicleRepository.save(vehicle);
    }

    public ParkingSpot park(Long parkingSpotId, Vehicle vehicle) {
        ParkingSpot parkingSpot = parkingSpotService.findBy(parkingSpotId);

        parkingSpot.setStatus(ParkingSpotStatus.OCCUPIED);
        parkingSpot.getVehicles().add(vehicle);
        vehicle.setParkingSpot(parkingSpot);

        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot parkAnywhere(Vehicle vehicle) {
        ParkingSpot parkingSpot = parkingSpotService.findAnyAvailable();

        parkingSpot.setStatus(ParkingSpotStatus.OCCUPIED);
        parkingSpot.getVehicles().add(vehicle);
        vehicle.setParkingSpot(parkingSpot);

        return parkingSpotRepository.save(parkingSpot);
    }

    public Vehicle findBy(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("cannot find vehicle by id: " + id));
    }

    public List<Vehicle> findAll() {
        Iterable<Vehicle> vehicles = vehicleRepository.findAll();
        return StreamSupport.stream(vehicles.spliterator(), false)
                .collect(Collectors.toList());
    }

}
