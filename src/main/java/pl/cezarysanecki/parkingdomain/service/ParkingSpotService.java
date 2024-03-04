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
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final VehicleRepository vehicleRepository;

    public ParkingSpot create() {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setStatus(ParkingSpotStatus.AVAILABLE);
        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot reservedAnyFor(Vehicle vehicle) {
        ParkingSpot parkingSpot = findAnyAvailable();

        if (parkingSpot.getReservedBy() != null && !parkingSpot.getReservedBy().getId().equals(vehicle.getId())) {
            throw new IllegalStateException("cannot reserve reserved parking spot");
        }
        if (parkingSpot.getStatus() != ParkingSpotStatus.AVAILABLE) {
            throw new IllegalStateException("cannot reserve unavailable parking spot");
        }

        parkingSpot.setStatus(ParkingSpotStatus.RESERVED);
        parkingSpot.setReservedBy(vehicle);

        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot reservedFor(Long parkingSpotId, Vehicle vehicle) {
        ParkingSpot parkingSpot = findBy(parkingSpotId);

        if (parkingSpot.getReservedBy() != null && !parkingSpot.getReservedBy().getId().equals(vehicle.getId())) {
            throw new IllegalStateException("cannot reserve reserved parking spot");
        }
        if (parkingSpot.getStatus() != ParkingSpotStatus.AVAILABLE) {
            throw new IllegalStateException("cannot reserve unavailable parking spot");
        }

        parkingSpot.setStatus(ParkingSpotStatus.RESERVED);
        parkingSpot.setReservedBy(vehicle);

        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot release(Long id) {
        ParkingSpot parkingSpot = findBy(id);

        parkingSpot.setStatus(ParkingSpotStatus.AVAILABLE);

        parkingSpot.getVehicles().forEach(vehicle -> vehicle.setParkingSpot(null));
        vehicleRepository.saveAll(parkingSpot.getVehicles());

        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot findAnyAvailable() {
        return findAll()
                .stream()
                .filter(parkingSpot -> parkingSpot.getStatus() == ParkingSpotStatus.AVAILABLE)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("cannot find available parking spot"));
    }

    public ParkingSpot findAnyAvailableFor(Vehicle vehicle) {
        List<ParkingSpot> parkingSpots = findAll();

        return parkingSpots.stream()
                .filter(parkingSpot -> !isFull(parkingSpot) && isTheSameType(vehicle, parkingSpot))
                .findAny()
                .or(() -> parkingSpots.stream()
                        .filter(parkingSpot -> parkingSpot.getStatus() == ParkingSpotStatus.AVAILABLE)
                        .findAny())
                .orElseThrow(() -> new IllegalArgumentException("cannot find available parking spot"));
    }

    public ParkingSpot deleteReservationOn(Long parkingSpotId) {
        ParkingSpot parkingSpot = findBy(parkingSpotId);
        parkingSpot.setReservedBy(null);
        if (parkingSpot.getVehicles().isEmpty()) {
            parkingSpot.setStatus(ParkingSpotStatus.AVAILABLE);
        } else {
            parkingSpot.setStatus(ParkingSpotStatus.OCCUPIED);
        }
        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot findBy(Long id) {
        return parkingSpotRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("cannot find parking spot by id: " + id));
    }

    public List<ParkingSpot> findAll() {
        Iterable<ParkingSpot> parkingSpots = parkingSpotRepository.findAll();
        return StreamSupport.stream(parkingSpots.spliterator(), false)
                .collect(Collectors.toList());
    }

    private static boolean isFull(ParkingSpot parkingSpot) {
        List<VehicleType> parkVehicleTypes = parkingSpot.getVehicles()
                .stream()
                .map(Vehicle::getType)
                .toList();

        return parkVehicleTypes.size() == 1 && parkVehicleTypes.get(0) == VehicleType.CAR
                || parkVehicleTypes.size() == 2 && parkVehicleTypes.stream().allMatch(type -> type == VehicleType.MOTORCYCLE)
                || parkVehicleTypes.size() == 3 && parkVehicleTypes.stream().allMatch(type -> type == VehicleType.BIKE || type == VehicleType.SCOOTER);
    }

    private static boolean isTheSameType(Vehicle vehicle, ParkingSpot parkingSpot) {
        List<VehicleType> parkVehicleTypes = parkingSpot.getVehicles()
                .stream()
                .map(Vehicle::getType)
                .toList();

        return !parkVehicleTypes.isEmpty() && parkVehicleTypes.contains(vehicle.getType());
    }

}
