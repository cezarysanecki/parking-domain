package pl.cezarysanecki.parkingdomain.catalogue.vehicle;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.catalogue.client.ClientId;

import java.util.UUID;

@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class Vehicle {

    @NonNull
    VehicleId vehicleId;
    @NonNull
    VehicleSize vehicleSize;
    @NonNull
    Make make;
    @NonNull
    Model model;
    @NonNull
    ClientId assignedTo;

    Vehicle(UUID vehicleId, int size, String make, String model, UUID clientId) {
        this(VehicleId.of(vehicleId), VehicleSize.of(size), new Make(make), new Model(model), ClientId.of(clientId));
    }

}

@Value
class Make {

    @NonNull
    String make;

    Make(String make) {
        if (make.isEmpty()) {
            throw new IllegalArgumentException("make cannot be empty");
        }
        this.make = make.trim();
    }

}

@Value
class Model {

    @NonNull
    String model;

    Model(String model) {
        if (model.isEmpty()) {
            throw new IllegalArgumentException("model cannot be empty");
        }
        this.model = model.trim();
    }

}
