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

    public Vehicle park(Long parkingSpotId, Vehicle vehicle) {
        ParkingSpot parkingSpot = parkingSpotRepository.findBy(parkingSpotId);

        if (parkingSpot.getVehicles().isEmpty()) {
            parkingSpot.setStatus(ParkingSpotStatus.OCCUPIED);
            parkingSpot.getVehicles().add(vehicle);
            vehicle.setParkingSpot(parkingSpot);

            vehicleRepository.save(vehicle);
            parkingSpotRepository.save(parkingSpot);

            return vehicle;
        }

        checkVehicleTypeRules(vehicle, parkingSpot);

        if (parkingSpot.isNotReservedFor(vehicle.getId())) {
            throw new IllegalStateException("cannot park on reserved parking spot");
        }

        parkingSpot.setStatus(ParkingSpotStatus.OCCUPIED);
        parkingSpot.getVehicles().add(vehicle);
        vehicle.setParkingSpot(parkingSpot);

        vehicleRepository.save(vehicle);
        parkingSpotRepository.save(parkingSpot);

        return vehicle;
    }

    public Vehicle parkAnywhere(Vehicle vehicle) {
        ParkingSpot parkingSpot = parkingSpotService.findAnyAvailableFor(vehicle);

        if (parkingSpot.getVehicles().isEmpty()) {
            parkingSpot.setStatus(ParkingSpotStatus.OCCUPIED);
            parkingSpot.getVehicles().add(vehicle);
            vehicle.setParkingSpot(parkingSpot);

            vehicleRepository.save(vehicle);
            parkingSpotRepository.save(parkingSpot);

            return vehicle;
        }

        if (parkingSpot.getVehicles().stream().anyMatch(vehicle1 -> vehicle1.getId().equals(vehicle.getId()))) {
            return vehicle;
        }

        checkVehicleTypeRules(vehicle, parkingSpot);

        if (parkingSpot.isNotReservedFor(vehicle.getId())) {
            throw new IllegalStateException("cannot park on reserved parking spot");
        }

        parkingSpot.setStatus(ParkingSpotStatus.OCCUPIED);
        parkingSpot.getVehicles().add(vehicle);
        vehicle.setParkingSpot(parkingSpot);

        vehicleRepository.save(vehicle);
        parkingSpotRepository.save(parkingSpot);

        return vehicle;
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

    private static void checkVehicleTypeRules(Vehicle vehicle, ParkingSpot parkingSpot) {
        List<VehicleType> parkVehicleTypes = parkingSpot.getVehicles()
                .stream()
                .map(Vehicle::getType)
                .toList();

        if (parkVehicleTypes.size() == 1 && parkVehicleTypes.get(0) == VehicleType.CAR) {
            throw new IllegalStateException("Parking spot is already occupied by car");
        }
        if (parkVehicleTypes.size() == 2 && parkVehicleTypes.stream().allMatch(type -> type == VehicleType.MOTORCYCLE)) {
            throw new IllegalStateException("Parking spot is already occupied by 2 motorcycles");
        }
        if (parkVehicleTypes.size() == 3 && parkVehicleTypes.stream().allMatch(type -> type == VehicleType.BIKE || type == VehicleType.SCOOTER)) {
            throw new IllegalStateException("Parking spot is already occupied by 3 bikes or scooters");
        }

        if (!parkVehicleTypes.contains(vehicle.getType())) {
            throw new IllegalStateException("Cannot mix vehicle types, this is for: " + parkVehicleTypes.get(0));
        }
    }

}
