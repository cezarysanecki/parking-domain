package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.control.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class ParkedVehicles {

    private final Set<Vehicle> collection;

    public static ParkedVehicles empty() {
        return new ParkedVehicles(Set.of());
    }

    public boolean contains(VehicleId vehicleId) {
        return collection.stream()
                .map(Vehicle::getVehicleId)
                .anyMatch(parkedVehicleId -> parkedVehicleId.equals(vehicleId));
    }

    public Option<Vehicle> findBy(VehicleId vehicleId) {
        return Option.ofOptional(
                collection.stream()
                        .filter(parkedVehicle -> parkedVehicle.getVehicleId().equals(vehicleId))
                        .findFirst());
    }

    public int occupation() {
        return collection.stream()
                .map(Vehicle::getVehicleSizeUnit)
                .map(VehicleSizeUnit::getValue)
                .reduce(0, Integer::sum);
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

}
