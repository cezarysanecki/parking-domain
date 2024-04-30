package pl.cezarysanecki.parkingdomain.catalogue.vehicle;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

interface CatalogueVehicleDatabase {

    void saveNew(Vehicle vehicle);

    class InMemoryCatalogueVehicleDatabase implements CatalogueVehicleDatabase {

        private static final Map<VehicleId, VehicleDatabaseRow> DATABASE = new ConcurrentHashMap<>();

        @Override
        public void saveNew(Vehicle vehicle) {
            DATABASE.put(
                    vehicle.getVehicleId(),
                    new VehicleDatabaseRow(
                            vehicle.getVehicleId().getValue(),
                            vehicle.getVehicleSize().getValue(),
                            vehicle.getMake().getMake(),
                            vehicle.getModel().getModel(),
                            vehicle.getAssignedTo().getValue()));
        }

    }

}

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class VehicleDatabaseRow {

    UUID vehicleId;
    int size;
    String make;
    String model;
    UUID clientId;

    Vehicle toVehicle() {
        return new Vehicle(vehicleId, size, make, model, clientId);
    }

}
